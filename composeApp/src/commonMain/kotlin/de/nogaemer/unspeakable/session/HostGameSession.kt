package de.nogaemer.unspeakable.session

import de.nogaemer.unspeakable.game.GameState
import de.nogaemer.unspeakable.model.GameEvent
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class HostGameSession : GameSession {
    private val _state = MutableStateFlow(GameState(isHost = true))
    override val state = _state.asStateFlow()

    private val clients = mutableListOf<DefaultWebSocketServerSession>()

    private var server: EmbeddedServer<*, *>? = null

    fun startServer(port: Int = 8080) {
        if (server != null) return

        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            install(WebSockets)
            routing {
                webSocket("/game") {
                    clients.add(this)
                    try {
                        for (frame in incoming) {
                            val textFrame = frame as? Frame.Text ?: continue
                            val event = Json.decodeFromString<GameEvent>(textFrame.readText())

                            // Server-side validation.
                            if (event is GameEvent.HostOnly) {
                                println("Warning: Client tried to send a Host-only event!")
                                continue
                            }

                            applyEvent(event)
                            broadcast(event)
                        }
                    } finally {
                        clients.remove(this)
                    }
                }
            }
        }.apply { start(wait = false) }
    }

    private suspend fun broadcast(event: GameEvent) {
        val json = Json.encodeToString(event)
        clients.toList().forEach { it.send(json) }
    }

    override suspend fun sendEvent(event: GameEvent) {
        applyEvent(event)
        broadcast(event)
    }

    suspend fun applyEvent(event: GameEvent) {
        _state.update { it.applyEvent(event) }
    }

    override fun close() {
        val runningServer = server ?: return
        server = null
        clients.clear()
        runningServer.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
    }
}