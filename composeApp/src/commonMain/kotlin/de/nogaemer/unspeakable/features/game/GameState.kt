package de.nogaemer.unspeakable.features.game

import de.nogaemer.unspeakable.core.model.GameEvent
import de.nogaemer.unspeakable.db.UnspeakableCard
import kotlinx.serialization.Serializable

data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val isHost: Boolean = false,
    val currentCard: UnspeakableCard? = null,
    val rounds: List<Round> = emptyList(),
    val me: Player? = null,
    val match: Match? = null,

    var maxRoundTime: Int? = null,
    var currentRoundTime: Int? = null,
) {
    suspend fun applyEvent(event: GameEvent): GameState {

        return when (event) {

            is GameEvent.StartRound -> TODO()

            is GameEvent.EndGame -> TODO()

            is GameEvent.Tick -> this.copy(
                currentRoundTime = event.currentRoundTime - 1
            )

            is GameEvent.SendMaxRoundTime -> this.copy(
                maxRoundTime = event.maxRoundTime
            )

            is GameEvent.SendCard -> this.copy(
                currentCard = event.card
            )

            is GameEvent.Buzz -> TODO()

            is GameEvent.Sabotage -> TODO()

            is GameEvent.AddPlayer -> addPlayer(event.player)

            else -> this
        }
    }

    fun addPlayer(player: Player) =
        this.copy(match = this.match?.copy(players = this.match.players + player))
}

@Serializable
data class Team(
    val name: String,
    val points: Int,
    val cards: List<UnspeakableCard>,
)

@Serializable
data class Match(
    val teams: List<Team>,
    val players: List<Player>
)

@Serializable
data class Round(
    val roundNumber: Int,
    val explainerTeam: Team,
    val cards: List<UnspeakableCard>,
    val points: Int,
)

@Serializable
data class Player(
    val name: String,
    val isHost: Boolean,
)

@Serializable
enum class GamePhase {
    SETUP,
    READY,
    PLAYING,
    ROUND_SUMMARY,
    GAME_OVER
}
