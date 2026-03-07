package de.nogaemer.unspeakable.game

import androidx.compose.runtime.currentRecomposeScope
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.model.GameEvent
import kotlinx.serialization.Serializable

data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val isHost: Boolean = false,
    val currentCard: UnspeakableCard? = null,
){
    suspend fun applyEvent(event: GameEvent): GameState {
        return when (event) {
            GameEvent.EndGame -> TODO()

            is GameEvent.SendCard -> this.copy(
                currentCard = event.card
            )

            is GameEvent.NewRandomCard -> this.copy(
                currentCard = Graph.dao.getRandomCard("de")
            )

            GameEvent.Buzz -> TODO()

            is GameEvent.Sabotage -> TODO()

            GameEvent.StartRound -> TODO()
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
