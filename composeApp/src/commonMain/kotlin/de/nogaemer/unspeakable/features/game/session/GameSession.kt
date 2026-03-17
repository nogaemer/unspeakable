package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.flow.StateFlow

interface GameSession {
    val state: StateFlow<GameState>
    suspend fun start()
    suspend fun sendEvent(event: GameClientEvent)
    fun close()
}