package de.nogaemer.unspeakable.features.game.session

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.features.game.GameState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * Connects to a host over websocket and mirrors host events into local state.
 */
class ClientSession(
    private val hostIp: String,
    private val playerName: String,
    private val profilePicture: ProfilePicture,
    private val scope: CoroutineScope,
) : GameSession {

    private val _state = MutableStateFlow(GameState(hostIp = hostIp))
    override val state: StateFlow<GameState> = _state.asStateFlow()

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 5.seconds
        }
    }
    private lateinit var session: DefaultWebSocketSession
    private var hostDisconnectHandled = false

    override suspend fun start() = connect(isReconnect = false)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e { "Unhandled session error: $throwable" }
        scope.launch { handleDrop() }
    }


    suspend fun connect(isReconnect: Boolean) {
        try {
            session = client.webSocketSession("ws://$hostIp:8080/game")

            val myId = if (isReconnect) state.value.me?.id ?: "" else ""
            val me = Player(myId, playerName, profilePicture, isHost = false, "")
            sendEvent(GameClientEvent.JoinGame(me))

        } catch (_: Exception) {
            onHostDisconnected()
            return
        }

        scope.launch(exceptionHandler) {
            try {
                for (frame in session.incoming) {
                    val event =
                        Json.decodeFromString<GameHostEvent>((frame as Frame.Text).readText())
                    _state.update { it.applyEvent(event) }
                }
                handleDrop()
            } catch (_: ClosedReceiveChannelException) {
                handleDrop()
            } catch (_: Exception) {
                handleDrop()
            }

        }
    }

    private suspend fun handleDrop() {
        val phase = _state.value.phase

        // Only attempt reconnect if a game was actually in progress
        if (phase == GamePhase.GAME_OVER || phase == GamePhase.CONNECTION_LOST) {
            _state.update { it.copy(phase = GamePhase.CONNECTION_LOST) }
            return
        }

        _state.update { it.copy(phase = GamePhase.RECONNECTING) }

        // Exponential backoff: 1s, 2s, 4s, 8s, … capped at 16s, give up after ~60s
        var attempt = 0
        val maxAttempts = 6
        while (attempt < maxAttempts) {
            val delayMs = minOf(1000L * (1 shl attempt), 16_000L)
            delay(delayMs)
            attempt++
            try {
                connect(isReconnect = true)
                return
            } catch (_: Exception) {
                // try again
            }
        }

        // All attempts failed
        _state.update { it.copy(phase = GamePhase.CONNECTION_LOST) }
    }


    private fun onHostDisconnected() {
        if (hostDisconnectHandled) return
        hostDisconnectHandled = true

        _state.update {
            it.copy(
                phase = GamePhase.CONNECTION_LOST,
                match = null,
                currentRound = null,
                currentCard = null,
                currentRoundTime = null,
                rounds = emptyList(),
            )
        }

        close()
    }

    override suspend fun sendEvent(event: GameClientEvent) =
        session.send(Json.encodeToString(event))

    override fun close() {
        scope.launch {
            if (::session.isInitialized) {
                sendEvent(GameClientEvent.LeaveGame)
                runCatching { session.close() }
            }
            client.close()
        }
    }
}
