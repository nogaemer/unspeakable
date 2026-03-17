package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.features.game.GameState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ClientSession(
    private val hostIp: String,
    private val playerName: String,
    private val profilePicture: ProfilePicture,
    private val scope: CoroutineScope,
) : GameSession {

    private val _state = MutableStateFlow(GameState())
    override val state: StateFlow<GameState> = _state.asStateFlow()

    private val client = HttpClient { install(WebSockets) }
    private lateinit var session: DefaultWebSocketSession

    override suspend fun start() {
        session = client.webSocketSession("ws://$hostIp:8080/game")

        val me = Player("", playerName, profilePicture, isHost = false)
        sendEvent(GameClientEvent.JoinGame(me))

        scope.launch {
            for (frame in session.incoming) {
                print(frame)
                val event = Json.decodeFromString<GameHostEvent>((frame as Frame.Text).readText())
                _state.update { it.applyEvent(event) }
            }
        }
    }

    override suspend fun sendEvent(event: GameClientEvent) =
        session.send(Json.encodeToString(event))

    override fun close() {
        scope.launch {
            session.close()
            client.close()
        }
    }
}
