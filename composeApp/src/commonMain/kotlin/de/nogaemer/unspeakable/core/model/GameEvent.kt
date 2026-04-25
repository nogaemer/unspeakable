package de.nogaemer.unspeakable.core.model

import de.nogaemer.unspeakable.core.mode.SoftConflictWarning
import de.nogaemer.unspeakable.db.UnspeakableCard
import kotlinx.serialization.Serializable

/**
 * Defines commands clients send to the host during match flow.
 */
@Serializable
sealed class GameClientEvent() {
    @Serializable data class JoinGame(val player: Player) : GameClientEvent()
    @Serializable data class AddLocalPlayer(val player: Player) : GameClientEvent()
    @Serializable data object LeaveGame : GameClientEvent()
    @Serializable data class JoinTeam(val team: Team) : GameClientEvent()
    @Serializable data object StartGame : GameClientEvent()
    @Serializable data class UpdateGameSettings(val settings: GameSettings) : GameClientEvent()

    @Serializable data object RequestNewRandomCard : GameClientEvent()
    @Serializable data object ReadyToStartMyTurn : GameClientEvent()
    @Serializable data object NextRoundOrEndGame : GameClientEvent()

    @Serializable data object CardCorrect : GameClientEvent()
    @Serializable data object CardSkipped : GameClientEvent()
    @Serializable data object CardWrong : GameClientEvent()
    @Serializable data class CardWrongByOpponent(val word: String) : GameClientEvent()

    @Serializable data class Sabotage(val newTabooWord: String) : GameClientEvent()

    fun toHostBoundEvent(playerId: String) = HostBoundClientEvent(playerId, this)

}

/**
 * Wraps client events with sender identity for host-side routing.
 */
@Serializable
data class HostBoundClientEvent(
    val playerId: String, val event: GameClientEvent
)


/**
 * Defines updates and commands emitted by the host to clients.
 */
@Serializable
sealed class GameHostEvent {
    @Serializable data class ModeConflict(val conflictingModeIds: List<String>) : GameHostEvent()
    @Serializable data class ModeWarning(val warningMessages: List<SoftConflictWarning>) : GameHostEvent()

    @Serializable data class Tick(val currentRoundTime: Int) : GameHostEvent()
    @Serializable data class SetRoundTime(val roundTime: Int) : GameHostEvent()
    @Serializable data class SendCard(val card: UnspeakableCard) : GameHostEvent()
    @Serializable data class PlayerJoined(val player: Player) : GameHostEvent()
    @Serializable data class YouJoined(val player: Player) : GameHostEvent()
    @Serializable data class PlayerLeft(val player: Player) : GameHostEvent()
    @Serializable data class PlayerJoinedTeam(val player: Player, val team: Team) : GameHostEvent()
    @Serializable data class SendMatch(val match: Match) : GameHostEvent()
    @Serializable data class SendRound(val round: Round?) : GameHostEvent()
    @Serializable data class SendPhase(val phase: GamePhase) : GameHostEvent()
    @Serializable data class SendGameSettings(val settings: GameSettings) : GameHostEvent()
    @Serializable data class StartGame(val match: Match) : GameHostEvent()

    @Serializable data class InitNewRound(val round: Round, val role: GameRole) : GameHostEvent()
    @Serializable data object StartRound: GameHostEvent()
    @Serializable data class ForbiddenWordViolated(val word: String, val durationMs: Long) : GameHostEvent()
    @Serializable data class CardPlayed(val playedCard: PlayedCard) : GameHostEvent()

    @Serializable data class SabotageUsed(val byPlayer: Player, val newTabooWord: String) : GameHostEvent()
    @Serializable data class SabotageDenied(val byPlayer: Player) : GameHostEvent()

    @Serializable
    data class ContaminationUpdated(val contaminatedWords: List<String>) : GameHostEvent()

    @Serializable data class EndRound(val completedRound: Round, val updatedTeams: List<Team>) : GameHostEvent()

    @Serializable data object EndGame : GameHostEvent()
}


