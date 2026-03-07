package de.nogaemer.unspeakable.session

import de.nogaemer.unspeakable.game.GameState
import de.nogaemer.unspeakable.model.GameEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalGameSession : GameSession {
    private val _state = MutableStateFlow(GameState(isHost = true))
    override val state = _state.asStateFlow()

    override suspend fun sendEvent(event: GameEvent) {
        applyEvent(event)
    }

    suspend fun applyEvent(event: GameEvent) {
        _state.update { it.applyEvent(event) }
    }

    override fun close() {}
}
