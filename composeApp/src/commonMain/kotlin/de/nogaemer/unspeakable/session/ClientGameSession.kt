package de.nogaemer.unspeakable.session

import de.nogaemer.unspeakable.game.GameState
import de.nogaemer.unspeakable.model.GameEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ClientGameSession(
    private val hostIp: String,
    private val scope: CoroutineScope
) : GameSession {
    private val _state = MutableStateFlow(GameState())
    override val state = _state.asStateFlow()

    private val client = HttpClient { install(WebSockets) }
    private lateinit var session: DefaultClientWebSocketSession

    suspend fun connect() {
        session = client.webSocketSession("ws://$hostIp:8080/game")
        scope.launch {
            for (frame in session.incoming) {
                val event = Json.decodeFromString<GameEvent>(
                    (frame as Frame.Text).readText()
                )
                _state.update { it.applyEvent(event) }
            }
        }
    }

    override suspend fun sendEvent(event: GameEvent) {
        session.send(Json.encodeToString(event))
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}