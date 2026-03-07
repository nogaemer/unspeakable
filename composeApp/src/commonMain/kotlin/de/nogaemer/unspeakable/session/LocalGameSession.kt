package de.nogaemer.unspeakable.session

import de.nogaemer.unspeakable.model.GameEvent

class LocalGameSession : HostAuthority() {

    override suspend fun broadcast(event: GameEvent) {

    }

    override suspend fun sendEvent(event: GameEvent) {
        handleSideEffects(event)
    }

    override fun close() {}
}
