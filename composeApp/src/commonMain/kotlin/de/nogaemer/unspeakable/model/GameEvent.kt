package de.nogaemer.unspeakable.model

import de.nogaemer.unspeakable.db.UnspeakableCard
import kotlinx.serialization.Serializable

@Serializable
sealed class GameEvent {
    interface HostOnly

    @Serializable
    data object StartRound : GameEvent(), HostOnly
    @Serializable
    data class Tick(val currentRoundTime: Int) : GameEvent(), HostOnly
    @Serializable
    data class SendMaxRoundTime(val maxRoundTime: Int) : GameEvent()

    @Serializable
    data object RequestNewRandomCard : GameEvent()
    @Serializable
    data class SendCard(val card: UnspeakableCard) : GameEvent(), HostOnly

    @Serializable
    data object EndGame : GameEvent(), HostOnly
    @Serializable
    data object Buzz : GameEvent()
    @Serializable
    data class Sabotage(val newTabooWord: String) : GameEvent()
}