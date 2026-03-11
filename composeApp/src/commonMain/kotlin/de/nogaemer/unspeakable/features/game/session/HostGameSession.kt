package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameEvent
import de.nogaemer.unspeakable.features.game.Player
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class HostGameSession(
    val hostPlayerName: String,
    scope: CoroutineScope
) : HostAuthority(scope) {

    private val connections = mutableMapOf<String, DefaultWebSocketServerSession>()

    private var server: EmbeddedServer<*, *>? = null

    fun startServer(port: Int = 8080) {
        if (server != null) return

        val me = Player(hostPlayerName, isHost = true)
        _state.update {
            it.copy(me = me)
        }

        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            handleSideEffects(GameEvent.AddPlayer(me))

            install(WebSockets)
            routing {
                webSocket("/game") {
                    var myClientName: String? = null
                    try {
                        for (frame in incoming) {
                            val textFrame = frame as? Frame.Text ?: continue
                            val event = Json.decodeFromString<GameEvent>(textFrame.readText())

                            if (event is GameEvent.JoinGame) {
                                myClientName = event.playerName
                                connections[myClientName] = this

                                sendEvent(GameEvent.AddPlayer(Player(myClientName, isHost = false)))
                            } else {
                                processClientEvent(this, event)
                            }

                        }
                    } finally {
                        myClientName?.let {
                            connections.remove(it)

                        }
                    }
                }
            }
        }.apply { start(wait = false) }
    }

    suspend fun processClientEvent(client: DefaultWebSocketServerSession, event: GameEvent) {

        if (event is GameEvent.HostOnly) {
            println("Warning: Client tried to send a Host-only event!")
            return
        }

        applyEvent(event)
        broadcast(event)
    }

    private suspend fun sendDirectMessage(playerId: String, event: GameEvent) {
        val session = connections[playerId]
        if (session != null) {
            val json = Json.encodeToString(event)
            session.send(Frame.Text(json))
        }
    }


    override suspend fun broadcast(event: GameEvent) {
        val json = Json.encodeToString(event)
        connections.values.forEach { it.send(json) }
    }

    override suspend fun sendEvent(event: GameEvent) {
        handleSideEffects(event)
    }

    override fun close() {
        val runningServer = server ?: return
        server = null
        connections.clear()
        runningServer.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
    }
}