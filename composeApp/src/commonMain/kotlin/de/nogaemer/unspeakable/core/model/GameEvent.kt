package de.nogaemer.unspeakable.core.model

import de.nogaemer.unspeakable.db.UnspeakableCard
import kotlinx.serialization.Serializable

@Serializable
sealed class GameClientEvent() {
    @Serializable
    data class JoinGame(
        val player: Player
    ) : GameClientEvent()

    @Serializable
    data object LeaveGame : GameClientEvent()

    @Serializable
    data class JoinTeam(val team: Team) : GameClientEvent()

    @Serializable
    data object StartGame : GameClientEvent()

    @Serializable
    data class UpdateGameSettings(val settings: GameSettings): GameClientEvent()

    @Serializable
    data object RequestNewRandomCard : GameClientEvent()

    @Serializable
    data object Buzz : GameClientEvent()

    @Serializable
    data object Sabotage : GameClientEvent()

    fun toHostBoundEvent(playerId: String) = HostBoundClientEvent(playerId, this)
}

@Serializable
data class HostBoundClientEvent(
    val playerId: String,
    val event: GameClientEvent
)


@Serializable
sealed class GameHostEvent {
    @Serializable
    data class Tick(val currentRoundTime: Int) : GameHostEvent()

    @Serializable
    data class SendCard(val card: UnspeakableCard) : GameHostEvent()

    @Serializable
    data class PlayerJoined(val player: Player) : GameHostEvent()

    @Serializable
    data class YouJoined(val player: Player) : GameHostEvent()

    @Serializable
    data class PlayerLeft(val player: Player) : GameHostEvent()

    @Serializable
    data class PlayerJoinedTeam(val player: Player, val team: Team) : GameHostEvent()

    @Serializable
    data class SendMatch(val match: Match) : GameHostEvent()

    @Serializable
    data class SendGameSettings(val settings: GameSettings) : GameHostEvent()

    @Serializable
    data class StartGame(val match: Match) : GameHostEvent()

    @Serializable
    data object StartRound : GameHostEvent()

    @Serializable
    data object EndGame : GameHostEvent()
}
