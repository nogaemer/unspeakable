package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameEvent
import kotlinx.coroutines.CoroutineScope

class LocalGameSession(scope: CoroutineScope) : HostAuthority(scope) {

    override suspend fun broadcast(event: GameEvent) {

    }

    override suspend fun sendEvent(event: GameEvent) {
        handleSideEffects(event)
    }

    override fun close() {}
}
