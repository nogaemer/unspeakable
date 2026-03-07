package de.nogaemer.unspeakable.session

import de.nogaemer.unspeakable.game.GameState
import de.nogaemer.unspeakable.model.GameEvent
import kotlinx.coroutines.flow.StateFlow

interface GameSession {
    val state: StateFlow<GameState>
    suspend fun sendEvent(event: GameEvent)
    fun close()
}