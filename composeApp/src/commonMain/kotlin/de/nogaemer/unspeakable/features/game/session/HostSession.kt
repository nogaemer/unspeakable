package de.nogaemer.unspeakable.features.game.session

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.db.UnspeakableCardsDao
import de.nogaemer.unspeakable.features.game.GameState
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HostSession(
    playerName: String,
    profilePicture: ProfilePicture,
    private val scope: CoroutineScope,
    cardDao: UnspeakableCardsDao,
    lang: String,
    private val port: Int = 8080,
) : GameSession {

    @OptIn(ExperimentalUuidApi::class)
    private val me =
        Player(Uuid.random().toString(), playerName, isHost = true, profilePicture = profilePicture)
    private val authority = GameAuthority(scope, cardDao, lang, me)

    override val state: StateFlow<GameState> = authority.state

    private val connections = mutableMapOf<String, DefaultWebSocketServerSession>()
    private var server: EmbeddedServer<*, *>? = null

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun start() {
        if (server != null) return

        // add host player before ws starts
        authority.processEvent(GameClientEvent.JoinGame(me).toHostBoundEvent(me.id))

        // subscribe to broadcast events
        scope.launch {
            authority.broadcastEvents.collect { event -> broadcast(event) }
        }
        scope.launch {
            authority.directEvents.collect { (id, event) -> sendDirect(id, event) }
        }

        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            install(WebSockets)
            routing {
                webSocket("/game") {
                    var client: Player? = null
                    try {
                        for (frame in incoming) {
                            val event =
                                Json.decodeFromString<GameClientEvent>((frame as Frame.Text).readText())

                            when (event) {
                                is GameClientEvent.JoinGame -> {
                                    client = event.player.copy(
                                        id = Uuid.random().toString(),
                                        isHost = true
                                    )
                                    connections[client.id] = this

                                    // give the new client a full snapshot before broadcasting AddPlayer
                                    authority.processEvent(
                                        GameClientEvent.JoinGame(client)
                                            .toHostBoundEvent(client.id)
                                    )
                                }

                                else -> authority.processEvent(
                                    event.toHostBoundEvent(
                                        client?.id ?: continue
                                    )
                                )
                            }
                        }
                    } finally {
                        if (client != null) {
                            authority.processEvent(GameClientEvent.LeaveGame.toHostBoundEvent(client.id))
                            client.let { connections.remove(it.name) }
                        }
                    }
                }
            }
        }.apply { start(wait = false) }
    }

    // host player - client interaction -> goes to authority skipping ws
    override suspend fun sendEvent(event: GameClientEvent) =
        authority.processEvent(event.toHostBoundEvent(me.id))

    private suspend fun sendDirect(playerId: String, event: GameHostEvent) {
        connections[playerId]?.send(Json.encodeToString(event))
    }

    private suspend fun broadcast(event: GameHostEvent) {
        val json = Json.encodeToString(event)
        connections.values.forEach { it.send(json) }
    }

    override fun close() {
        Logger.d { "Closing host session" }
        authority.close()
        server?.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
        server = null
        connections.clear()
    }
}
