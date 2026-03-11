package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameEvent
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.flow.StateFlow

interface GameSession {
    val state: StateFlow<GameState>
    suspend fun sendEvent(event: GameEvent)
    fun close()
}