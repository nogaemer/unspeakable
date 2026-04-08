package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines the lifecycle and messaging contract for a game session runtime.
 */
interface GameSession {
    val state: StateFlow<GameState>
    suspend fun start()
    suspend fun sendEvent(event: GameClientEvent)
    suspend fun sendEventAs(event: GameClientEvent, playerId: String) = sendEvent(event)
    fun close()
}