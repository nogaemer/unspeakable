package de.nogaemer.unspeakable.features.game.session

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.mode.CompatibilityResult
import de.nogaemer.unspeakable.core.mode.ModeChain
import de.nogaemer.unspeakable.core.mode.ModeCompatibility
import de.nogaemer.unspeakable.core.mode.ModeRegistry
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.CardOutcome.CORRECT
import de.nogaemer.unspeakable.core.model.CardOutcome.SKIPPED
import de.nogaemer.unspeakable.core.model.CardOutcome.WRONG
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameClientEvent.JoinTeam
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.GameHostEvent.ForbiddenWordViolated
import de.nogaemer.unspeakable.core.model.GameHostEvent.PlayerJoined
import de.nogaemer.unspeakable.core.model.GameHostEvent.PlayerJoinedTeam
import de.nogaemer.unspeakable.core.model.GameHostEvent.PlayerLeft
import de.nogaemer.unspeakable.core.model.GameHostEvent.SendCard
import de.nogaemer.unspeakable.core.model.GameHostEvent.SendGameSettings
import de.nogaemer.unspeakable.core.model.GameHostEvent.SendMatch
import de.nogaemer.unspeakable.core.model.GameHostEvent.StartGame
import de.nogaemer.unspeakable.core.model.GameHostEvent.YouJoined
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.core.model.GameRole
import de.nogaemer.unspeakable.core.model.HostBoundClientEvent
import de.nogaemer.unspeakable.core.model.Match
import de.nogaemer.unspeakable.core.model.PlayedCard
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.core.model.Team
import de.nogaemer.unspeakable.db.UnspeakableCardDto
import de.nogaemer.unspeakable.db.UnspeakableCardsDao
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

/**
 * Applies client events, manages round progression, and emits host events.
 */
class GameAuthority(
    private val scope: CoroutineScope,
    private val cardDao: UnspeakableCardsDao,
    private val lang: String,
    hostPlayer: Player,
    private val isLocalGame: Boolean,
) {
    private val _state = MutableStateFlow(
        GameState(
            isHost = true,
            me = hostPlayer,
            match = Match(
                players = listOf(),
                teams = listOf(
                    Team(id = "team_1", name = "Team 1", players = listOf()),
                    Team(id = "team_2", name = "Team 2", players = listOf()),
                ),
            ),
            isLocalGame = isLocalGame
        )
    )
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _broadcastEvents = MutableSharedFlow<GameHostEvent>(extraBufferCapacity = 64)
    val broadcastEvents: SharedFlow<GameHostEvent> = _broadcastEvents.asSharedFlow()

    private val _directEvents =
        MutableSharedFlow<Pair<String, GameHostEvent>>(extraBufferCapacity = 64)
    val directEvents: SharedFlow<Pair<String, GameHostEvent>> = _directEvents.asSharedFlow()

    private lateinit var timer: Timer

    private var nextExplainerTeamIndex = 0
    private val nextExplainerIndexPerTeam = mutableMapOf<String, Int>()
    private var resolvingWrongByOpponent = false

    private var modeChain: ModeChain = ModeChain(emptyList())


    /**
     * Processes a [HostBoundClientEvent] and updates the game state accordingly.
     * Handles all client-initiated actions, such as joining/leaving, updating settings,
     * starting the game, card actions, sabotage, and round transitions.
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun processEvent(boundClientEvent: HostBoundClientEvent) {
        Logger.d { "Received client event from ${boundClientEvent.playerId}: ${boundClientEvent.event}" }

        when (val event = boundClientEvent.event) {

            is GameClientEvent.JoinGame -> {
                applyAndBroadcast(PlayerJoined(event.player))
                processEvent(
                    JoinTeam(_state.value.match!!.teams.first()).toHostBoundEvent(event.player.id)
                )
                sendDirect(event.player.id, SendMatch(_state.value.match!!))
                sendDirect(event.player.id, YouJoined(_state.value.getPlayer(event.player.id)!!))
            }

            is GameClientEvent.LeaveGame -> {
                val player = _state.value.getPlayer(boundClientEvent.playerId) ?: return
                applyAndBroadcast(PlayerLeft(player))
            }

            is JoinTeam -> {
                val player = _state.value.getPlayer(boundClientEvent.playerId) ?: return
                val team = _state.value.getTeam(event.team.id) ?: return
                val currentTeam = _state.value.getTeamByPlayer(player)
                if (currentTeam?.id == team.id) return

                applyAndBroadcast(PlayerJoinedTeam(player, team))
            }

            is GameClientEvent.UpdateGameSettings -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return

                val conflictEvent = checkModeCompatibility(event.settings.enabledModeIds)
                if (conflictEvent != null) applyAndBroadcast(conflictEvent)

                applyAndBroadcast(SendGameSettings(event.settings))
            }

            is GameClientEvent.StartGame -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return
                val match = _state.value.match ?: return

                val teams = match.teams.filter { it.players.isNotEmpty() }
                if (teams.isEmpty()) return

                val conflictEvent = checkModeCompatibility(match.settings.enabledModeIds)
                if (conflictEvent is GameHostEvent.ModeConflict) {
                    applyAndBroadcast(conflictEvent)
                    return
                }

                modeChain = buildModeChain(match.settings.enabledModeIds)


                applyAndBroadcast(StartGame(match))
                initNextRound()
            }

            GameClientEvent.RequestNewRandomCard -> {
                if (_state.value.currentRound == null) return
                val card = getRandomCard() ?: return
                applyAndBroadcast(SendCard(card.toUnspeakableCard()))
            }


            GameClientEvent.CardCorrect -> {
                if (resolvingWrongByOpponent) return
                if (boundClientEvent.playerId != _state.value.currentExplainer?.id) return

                handleCardOutcome(CORRECT)
            }

            GameClientEvent.CardSkipped -> {
                if (resolvingWrongByOpponent) return
                if (boundClientEvent.playerId != _state.value.currentExplainer?.id) return

                handleCardOutcome(SKIPPED)
            }

            GameClientEvent.CardWrong -> {
                if (resolvingWrongByOpponent) return
                if (boundClientEvent.playerId != _state.value.currentExplainer?.id) return

                handleCardOutcome(WRONG)
            }


            is GameClientEvent.Sabotage -> {
                // Only allow if SabotageMode is active — otherwise ignore
                val modeActive = modeChain.hasMode("sabotage")
                if (!modeActive) return

                // Validate: sabotage must come from the OPPOSING team
                val currentRound = _state.value.currentRound ?: return
                val buzzer = _state.value.getPlayer(boundClientEvent.playerId) ?: return
                val isOpposingTeam = currentRound.explainerTeam.players.none { it.id == buzzer.id }
                if (!isOpposingTeam) return

                // Delegate to the mode chain
                val extraEvents = modeChain.onSabotage(buzzer, sabotageWord = event.newTabooWord)
                extraEvents.forEach { applyAndBroadcast(it) }
            }

            GameClientEvent.ReadyToStartMyTurn -> {
                if (boundClientEvent.playerId != _state.value.currentExplainer?.id) return
                startRound()
            }

            GameClientEvent.NextRoundOrEndGame -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return
                if (_state.value.phase != GamePhase.ROUND_SUMMARY) return
                initNextRound()
            }

            is GameClientEvent.CardWrongByOpponent -> {
                if (!::timer.isInitialized || !timer.isRunning) return
                if (resolvingWrongByOpponent) return
                if (_state.value.phase != GamePhase.PLAYING) return

                val currentCard = _state.value.currentCard ?: return
                val senderTeam = _state.value.getTeamByPlayerId(boundClientEvent.playerId) ?: return
                val explainerTeamId = _state.value.currentExplainerTeam?.id ?: return
                if (senderTeam.id == explainerTeamId) return

                val isForbiddenWord = currentCard.forbiddenWords.any { it.equals(event.word, ignoreCase = true) }
                if (!isForbiddenWord) return

                resolvingWrongByOpponent = true
                val durationMs = 700L
                val violatedWord = event.word.uppercase()

                applyAndBroadcast(ForbiddenWordViolated(violatedWord, durationMs))

                scope.launch {
                    delay(durationMs)
                    handleCardOutcome(WRONG)
                    resolvingWrongByOpponent = false
                }
            }

            is GameClientEvent.AddLocalPlayer -> {
                if (!isLocalGame) return
                applyAndBroadcast(PlayerJoined(event.player))
                val team = _state.value.match!!.teams
                    .firstOrNull { it.id == event.player.teamId }
                    ?: _state.value.match!!.teams.first()
                applyAndBroadcast(PlayerJoinedTeam(event.player, team))
            }
        }
    }

    /**
     * Initializes the next round, assigning explainer and teams, and starts the timer.
     * Handles round rotation, explainer selection, and notifies all players of their roles.
     * If max rounds reached, ends the game.
     */
    private suspend fun initNextRound() {
        val match = _state.value.match ?: return
        val settings = match.settings
        val completedRounds = _state.value.rounds.size

        if (completedRounds >= settings.maxRounds) {
            applyAndBroadcast(GameHostEvent.EndGame)
            return
        }

        val teams = match.teams.filter { it.players.isNotEmpty() }
        if (teams.isEmpty()) return

        val explainerTeam = teams[nextExplainerTeamIndex % teams.size]
        val opponentTeams =
            _state.value.match?.teams?.filter { it.id != explainerTeam.id } ?: return
        nextExplainerTeamIndex++

        val teamPlayers = explainerTeam.players
        val explainerIndex = nextExplainerIndexPerTeam[explainerTeam.id] ?: 0
        val explainerPlayer = teamPlayers[explainerIndex % teamPlayers.size]
        nextExplainerIndexPerTeam[explainerTeam.id] = explainerIndex + 1

        val round = Round(
            roundNumber = completedRounds + 1,
            explainerTeam = explainerTeam,
            explainerPlayer = explainerPlayer,
        )

        // Ask modes if they want a different start time
        val startTime = modeChain.resolveRoundStartTime(settings.roundTime)


        timer = Timer(
            scope = scope,
            maxTime = startTime,
            onTick = { tick ->
                val (consumed, extraEvents) = modeChain.onTick(tick)
                if (!consumed) applyAndBroadcast(GameHostEvent.Tick(tick))
                extraEvents.forEach { applyAndBroadcast(it) }
            },
            onFinish = { endCurrentRound() }
        )

        val extraEvents = modeChain.onRoundInit(round)

        // Send round to all teams with appropriate role assignment
        opponentTeams.forEach {
            sendToTeam(
                it.id,
                GameHostEvent.InitNewRound(round, GameRole.OPPONENT)
            )
        }
        explainerTeam.players.filter { it.id != explainerPlayer.id }.forEach { player ->
            sendDirect(player.id, GameHostEvent.InitNewRound(round, GameRole.GUESSER))
        }
        sendDirect(explainerPlayer.id, GameHostEvent.InitNewRound(round, GameRole.EXPLAINER))

        val card = getRandomCard()?.toUnspeakableCard()
        if (card != null) applyAndBroadcast(SendCard(card))

        extraEvents.forEach { applyAndBroadcast(it) }
    }

    /**
     * Starts the round timer and broadcasts the start event.
     * Notifies all modes of round start.
     */
    private suspend fun startRound() {
        timer.start()
        applyAndBroadcast(GameHostEvent.StartRound)

        val extraEvents = modeChain.onRoundStart()
        extraEvents.forEach { applyAndBroadcast(it) }
    }

    /**
     * Handles the outcome of a played card (correct, skipped, wrong).
     * Updates the timer, applies mode effects, and deals the next card if time remains.
     */
    private suspend fun handleCardOutcome(outcome: CardOutcome) {
        val currentCard = _state.value.currentCard ?: return
        val playedCard = PlayedCard(card = currentCard, outcome = outcome)
        applyAndBroadcast(GameHostEvent.CardPlayed(playedCard))

        val result = modeChain.onCardPlayed(playedCard)

        // Apply time delta to the real timer and broadcast the updated tick
        result.timeDelta?.let { delta ->
            timer.addTime(delta)
            applyAndBroadcast(GameHostEvent.Tick(timer.timeLeft))
        }

        result.extraEvents.forEach { applyAndBroadcast(it) }



        if (timer.isRunning) {
            val nextCard = getRandomCard()?.toUnspeakableCard() ?: return
            val mutatedCard = modeChain.onCardDealt(nextCard)
            applyAndBroadcast(SendCard(mutatedCard))
        }
    }

    /**
     * Ends the current round, updates team points, and broadcasts round summary.
     * Notifies all modes of round end.
     */
    private suspend fun endCurrentRound() {
        val completedRound = _state.value.currentRound ?: return
        val elapsedSeconds = (timer.maxTime - timer.timeLeft).coerceIn(0, timer.maxTime)
        val completedRoundWithDuration = completedRound.copy(durationSeconds = elapsedSeconds)
        timer.reset()

        val match = _state.value.match ?: return
        val updatedTeams = match.teams.map { team ->
            if (team.id == completedRoundWithDuration.explainerTeam.id)
                team.copy(points = team.points + completedRoundWithDuration.points)
            else team
        }
        _state.update { it.copy(match = match.copy(teams = updatedTeams)) }

        applyAndBroadcast(GameHostEvent.EndRound(completedRoundWithDuration, updatedTeams))

        val extraEvents = modeChain.onRoundEnd(round = completedRoundWithDuration)
        extraEvents.forEach { applyAndBroadcast(it) }
    }

    // ── Mode helpers ─────────────────────────────────────────────────────

    /**
     * Checks compatibility for a set of mode IDs.
     * Returns a [GameHostEvent.ModeConflict] or [GameHostEvent.ModeWarning]
     * to broadcast, or null if everything is compatible.
     */
    private fun checkModeCompatibility(enabledIds: Set<String>): GameHostEvent? {
        val modes = ModeRegistry.resolvedModes(enabledIds)
        return when (val result = ModeCompatibility.check(modes)) {
            is CompatibilityResult.Incompatible ->
                GameHostEvent.ModeConflict(
                    result.conflicts.map { it.toString() }
                )
            is CompatibilityResult.SoftWarning ->
                GameHostEvent.ModeWarning(
                    result.warnings
                )
            CompatibilityResult.Compatible -> null
        }
    }

    /**
     * Instantiates a [ModeChain] from the given mode IDs.
     * Only called at actual game start — never during lobby setup.
     */
    private fun buildModeChain(enabledIds: Set<String>): ModeChain {
        val modes = ModeRegistry.resolvedModes(enabledIds)
        Logger.i { "Building ModeChain with: ${modes.map { it.id }}" }
        return ModeChain(modes)
    }

    private suspend fun getRandomCard(): UnspeakableCardDto? {
        val selectedCategories = _state.value.match?.settings?.selectedCategoryIds.orEmpty()
        return if (selectedCategories.isEmpty()) {
            cardDao.getRandomCard(lang)
        } else {
            cardDao.getRandomCardByCategories(lang, selectedCategories.toList())
                ?: cardDao.getRandomCard(lang)
        }
    }

    /**
     * Applies a [GameHostEvent] to the game state and broadcasts it to all clients.
     */
    private suspend fun applyAndBroadcast(event: GameHostEvent) {
        Logger.d { "Applying/Broadcasting event: $event" }

        _state.update { it.applyEvent(event) }
        _broadcastEvents.emit(event)
    }

    /**
     * Sends a [GameHostEvent] to all players in a specific team.
     */
    private suspend fun sendToTeam(teamId: String, event: GameHostEvent) {
        Logger.d { "Sending event to team $teamId: $event" }

        _state.value.getTeam(teamId)?.players?.forEach { player ->
            sendDirect(player.id, event)
        }
    }

    /**
     * Sends a [GameHostEvent] directly to a specific player.
     * If the player is the host, also applies the event to local state.
     */
    private suspend fun sendDirect(playerId: String, event: GameHostEvent) {
        Logger.d { "Sending direct event to $playerId: $event" }
        if (playerId == _state.value.me?.id) _state.update { it.applyEvent(event) }

        _directEvents.emit(playerId to event)
    }

    /**
     * Cleans up resources and resets the timer if initialized.
     */
    fun close() = if (::timer.isInitialized) timer.reset() else Unit
}
