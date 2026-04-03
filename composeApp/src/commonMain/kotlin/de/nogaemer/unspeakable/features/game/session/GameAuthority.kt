package de.nogaemer.unspeakable.features.game.session

import co.touchlab.kermit.Logger
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
            )
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
                sendDirect(event.player.id, YouJoined(event.player))
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
                applyAndBroadcast(SendGameSettings(event.settings))
            }

            is GameClientEvent.StartGame -> {
                Logger.d { "boundClientEvent.playerId: ${boundClientEvent.playerId}, _state.value.me?.id: ${_state.value.me?.id}" }
                if (boundClientEvent.playerId != _state.value.me?.id) return
                val match = _state.value.match ?: return
                applyAndBroadcast(StartGame(match))
                initNextRound()
            }

            GameClientEvent.RequestNewRandomCard -> {
                if (_state.value.currentRound == null) return
                val card = cardDao.getRandomCard(lang) ?: return
                applyAndBroadcast(SendCard(card))
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

            is GameClientEvent.Buzz -> TODO()

            is GameClientEvent.Sabotage -> TODO()

            GameClientEvent.ReadyToStartMyTurn -> {
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

                val isForbiddenWord = listOf(
                    currentCard.forbidden1,
                    currentCard.forbidden2,
                    currentCard.forbidden3,
                    currentCard.forbidden4,
                    currentCard.forbidden5,
                ).any { it.equals(event.word, ignoreCase = true) }
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
        }
    }

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
        val opponentTeams = _state.value.match?.teams?.filter { it.id != explainerTeam.id } ?: return
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

        timer = Timer(
            scope = scope,
            maxTime = settings.roundTime,
            onTick = { tick -> applyAndBroadcast(GameHostEvent.Tick(tick)) },
            onFinish = { endCurrentRound() }
        )

        // Send round to all teams with appropriate role assignment
        opponentTeams.forEach { sendToTeam(it.id, GameHostEvent.InitNewRound(round,GameRole.OPPONENT)) }
        explainerTeam.players.filter { it.id != explainerPlayer.id }.forEach { player ->
            sendDirect(player.id, GameHostEvent.InitNewRound(round,GameRole.GUESSER))
        }
        sendDirect(explainerPlayer.id, GameHostEvent.InitNewRound(round,GameRole.EXPLAINER))

        val card = cardDao.getRandomCard(lang)
        if (card != null) applyAndBroadcast(GameHostEvent.SendCard(card))
    }

    private suspend fun startRound() {
        timer.start()
        applyAndBroadcast(GameHostEvent.StartRound)
    }

    private suspend fun handleCardOutcome(outcome: CardOutcome) {
        val currentCard = _state.value.currentCard ?: return
        val playedCard = PlayedCard(card = currentCard, outcome = outcome)
        applyAndBroadcast(GameHostEvent.CardPlayed(playedCard))

        if (timer.isRunning) {
            val nextCard = cardDao.getRandomCard(lang) ?: return
            applyAndBroadcast(GameHostEvent.SendCard(nextCard))
        }
    }

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
    }


    private suspend fun applyAndBroadcast(event: GameHostEvent) {
        Logger.d { "Applying/Broadcasting event: $event" }

        _state.update { it.applyEvent(event) }
        _broadcastEvents.emit(event)
    }

    private suspend fun sendToTeam(teamId: String, event: GameHostEvent) {
        Logger.d { "Sending event to team $teamId: $event" }

        _state.value.getTeam(teamId)?.players?.forEach { player ->
            sendDirect(player.id, event)
        }
    }

    private suspend fun sendDirect(playerId: String, event: GameHostEvent) {
        Logger.d { "Sending direct event to $playerId: $event" }
        if (playerId == _state.value.me?.id) _state.update { it.applyEvent(event) }

        _directEvents.emit(playerId to event)
    }

    fun close() = if (::timer.isInitialized) timer.reset() else Unit
}
