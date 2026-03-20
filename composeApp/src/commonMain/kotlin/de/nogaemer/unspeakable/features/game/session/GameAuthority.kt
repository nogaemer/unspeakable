package de.nogaemer.unspeakable.features.game.session

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.CardOutcome.CORRECT
import de.nogaemer.unspeakable.core.model.CardOutcome.SKIPPED
import de.nogaemer.unspeakable.core.model.CardOutcome.WRONG
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameClientEvent.JoinTeam
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.HostBoundClientEvent
import de.nogaemer.unspeakable.core.model.Match
import de.nogaemer.unspeakable.core.model.PlayedCard
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.core.model.Team
import de.nogaemer.unspeakable.db.UnspeakableCardsDao
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi

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

    //HostSession subscribes to it
    private val _broadcastEvents = MutableSharedFlow<GameHostEvent>(extraBufferCapacity = 64)
    val broadcastEvents: SharedFlow<GameHostEvent> = _broadcastEvents.asSharedFlow()

    private val _directEvents =
        MutableSharedFlow<Pair<String, GameHostEvent>>(extraBufferCapacity = 64)
    val directEvents: SharedFlow<Pair<String, GameHostEvent>> = _directEvents.asSharedFlow()

    private lateinit var timer: Timer;

    private var nextExplainerTeamIndex = 0
    private val nextExplainerIndexPerTeam = mutableMapOf<String, Int>()


    @OptIn(ExperimentalUuidApi::class)
    suspend fun processEvent(boundClientEvent: HostBoundClientEvent) {
        Logger.d { "Received client event from ${boundClientEvent.playerId}: ${boundClientEvent.event}" }

        when (val event = boundClientEvent.event) {

            is GameClientEvent.JoinGame -> {
                applyAndBroadcast(GameHostEvent.PlayerJoined(event.player))
                processEvent(
                    JoinTeam(_state.value.match!!.teams.first()).toHostBoundEvent(event.player.id)
                )
                sendDirect(event.player.id, GameHostEvent.SendMatch(_state.value.match!!))
                sendDirect(event.player.id, GameHostEvent.YouJoined(event.player))
            }

            is GameClientEvent.LeaveGame -> {
                val player = _state.value.getPlayer(boundClientEvent.playerId) ?: return
                applyAndBroadcast(GameHostEvent.PlayerLeft(player))
            }

            is JoinTeam -> {
                val player = _state.value.getPlayer(boundClientEvent.playerId) ?: return
                applyAndBroadcast(GameHostEvent.PlayerJoinedTeam(player, event.team))
            }

            is GameClientEvent.UpdateGameSettings -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return
                applyAndBroadcast(GameHostEvent.SendGameSettings(event.settings))
            }

            is GameClientEvent.StartGame -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return
                val match = _state.value.match ?: return
                applyAndBroadcast(GameHostEvent.StartGame(match))
                initNextRound()
            }

            GameClientEvent.RequestNewRandomCard -> {
                // Only valid mid-round
                if (_state.value.currentRound == null) return
                val card = cardDao.getRandomCard(lang) ?: return
                applyAndBroadcast(GameHostEvent.SendCard(card))
            }

            // ── Card outcome events ───────────────────────────────────────

            GameClientEvent.CardCorrect -> handleCardOutcome(CORRECT)
            GameClientEvent.CardSkipped -> handleCardOutcome(SKIPPED)
            GameClientEvent.CardWrong -> handleCardOutcome(WRONG)

            is GameClientEvent.Buzz -> TODO()

            is GameClientEvent.Sabotage -> TODO()

            GameClientEvent.ReadyToStartMyTurn -> {
                startRound();
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

        applyAndBroadcast(GameHostEvent.InitNewRound(round))

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
        timer.reset()

        val match = _state.value.match ?: return
        val updatedTeams = match.teams.map { team ->
            if (team.id == completedRound.explainerTeam.id)
                team.copy(points = team.points + completedRound.points)
            else team
        }
        _state.update { it.copy(match = match.copy(teams = updatedTeams)) }

        applyAndBroadcast(GameHostEvent.EndRound(completedRound))
    }


    private suspend fun applyAndBroadcast(event: GameHostEvent) {
        Logger.d { "Applying/Broadcasting event: $event" }

        _state.update { it.applyEvent(event) }
        _broadcastEvents.emit(event)
    }

    private suspend fun sendDirect(playerId: String, event: GameHostEvent) {
        Logger.d { "Sending direct event to $playerId: $event" }

        _directEvents.emit(playerId to event)
    }

    fun close() = if (::timer.isInitialized) timer.reset() else Unit
}
