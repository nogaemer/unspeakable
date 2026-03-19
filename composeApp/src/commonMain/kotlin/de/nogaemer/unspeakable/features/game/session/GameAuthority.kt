package de.nogaemer.unspeakable.features.game.session

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameClientEvent.JoinTeam
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.GameHostEvent.PlayerJoined
import de.nogaemer.unspeakable.core.model.GameHostEvent.PlayerJoinedTeam
import de.nogaemer.unspeakable.core.model.GameHostEvent.PlayerLeft
import de.nogaemer.unspeakable.core.model.GameHostEvent.SendCard
import de.nogaemer.unspeakable.core.model.HostBoundClientEvent
import de.nogaemer.unspeakable.core.model.Match
import de.nogaemer.unspeakable.core.model.Player
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
                    Team(
                        name = "Team 1",
                        players = listOf(),
                        points = 0,
                        cards = listOf()
                    ),
                    Team(
                        name = "Team 2",
                        players = listOf(),
                        points = 0,
                        cards = listOf()
                    )
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

    @OptIn(ExperimentalUuidApi::class)
    suspend fun processEvent(boundClientEvent: HostBoundClientEvent) {
        Logger.d { "Received client event from ${boundClientEvent.playerId}: ${boundClientEvent.event}" }

        when (val event = boundClientEvent.event) {
            GameClientEvent.RequestNewRandomCard -> {
                if (!::timer.isInitialized) return

                timer.reset(); timer.start()
                val card = cardDao.getRandomCard(lang) ?: return
                applyAndBroadcast(SendCard(card))
            }

            is GameClientEvent.JoinGame -> {
                applyAndBroadcast(
                    PlayerJoined(event.player)
                )
                processEvent(
                    JoinTeam(
                        _state.value.match!!.teams.first()
                    ).toHostBoundEvent(event.player.id)
                )

                sendDirect(event.player.id, GameHostEvent.SendMatch(_state.value.match!!))
                sendDirect(event.player.id, GameHostEvent.YouJoined(event.player))
            }

            is GameClientEvent.Buzz -> TODO()

            is GameClientEvent.Sabotage -> TODO()

            is GameClientEvent.JoinTeam -> {
                val player = _state.value.getPlayer(boundClientEvent.playerId) ?: return

                applyAndBroadcast(
                    PlayerJoinedTeam(
                        player,
                        event.team
                    )
                )
            }

            is GameClientEvent.StartGame -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return

                timer = Timer(
                    scope = this.scope,
                    maxTime = _state.value.match?.settings?.roundTime ?: return,
                    onTick = { tick -> applyAndBroadcast(GameHostEvent.Tick(tick)) }
                )

                applyAndBroadcast(GameHostEvent.StartGame(_state.value.match ?: return))

                timer.reset(); timer.start()
            }

            is GameClientEvent.LeaveGame -> {
                val player = _state.value.getPlayer(boundClientEvent.playerId) ?: return

                applyAndBroadcast(PlayerLeft(player))
            }

            is GameClientEvent.UpdateGameSettings -> {
                if (boundClientEvent.playerId != _state.value.me?.id) return

                applyAndBroadcast(GameHostEvent.SendGameSettings(event.settings))
            }
        }

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

    fun close() = timer.reset()
}
