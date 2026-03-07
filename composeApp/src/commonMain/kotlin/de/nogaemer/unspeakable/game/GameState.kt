package de.nogaemer.unspeakable.game

import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.model.GameEvent
import kotlinx.serialization.Serializable

data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val isHost: Boolean = false,
    val currentCard: UnspeakableCard? = null,

    var maxRoundTime: Int? = null,
    var currentRoundTime: Int? = null,
) {
    suspend fun applyEvent(event: GameEvent): GameState {
        return when (event) {
            is GameEvent.EndGame -> TODO()

            is GameEvent.SendCard -> this.copy(
                currentCard = event.card
            )

            is GameEvent.RequestNewRandomCard -> TODO()

            is GameEvent.Buzz -> TODO()

            is GameEvent.Sabotage -> TODO()

            is GameEvent.StartRound -> TODO()

            is GameEvent.Tick -> this.copy(
                currentRoundTime = (event.currentRoundTime ?: 0) - 1
            )

            is GameEvent.SendMaxRoundTime -> this.copy(
                maxRoundTime = event.maxRoundTime
            )
        }
    }
}

@Serializable
enum class GamePhase {
    SETUP,
    READY,
    PLAYING,
    ROUND_SUMMARY,
    GAME_OVER
}
