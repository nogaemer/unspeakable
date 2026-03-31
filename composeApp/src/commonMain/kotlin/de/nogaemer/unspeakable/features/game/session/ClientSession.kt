package de.nogaemer.unspeakable.features.game.session

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
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

    private val _state = MutableStateFlow(GameState())
    override val state: StateFlow<GameState> = _state.asStateFlow()

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 5.seconds
        }
    }
    private lateinit var session: DefaultWebSocketSession
    private var hostDisconnectHandled = false

    override suspend fun start() {
        try {
            session = client.webSocketSession("ws://$hostIp:8080/game")

            val me = Player("", playerName, profilePicture, isHost = false)
            sendEvent(GameClientEvent.JoinGame(me))
        } catch (_: Exception) {
            onHostDisconnected()
            return
        }

        scope.launch {
            try {
                for (frame in session.incoming) {
                    val event =
                        Json.decodeFromString<GameHostEvent>((frame as Frame.Text).readText())
                    _state.update { it.applyEvent(event) }
                }
                onHostDisconnected()
            } catch (_: ClosedReceiveChannelException) {
                onHostDisconnected()
            } catch (_: Exception) {
                onHostDisconnected()
            }
        }
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
                runCatching { session.close() }
            }
            client.close()
        }
    }
}
