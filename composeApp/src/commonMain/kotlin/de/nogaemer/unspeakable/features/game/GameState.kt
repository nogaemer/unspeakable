package de.nogaemer.unspeakable.features.game

import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.core.model.Match
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.core.model.Team
import de.nogaemer.unspeakable.db.UnspeakableCard

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
    suspend fun applyEvent(event: GameHostEvent): GameState = when (event) {

        is GameHostEvent.Tick -> copy(currentRoundTime = event.currentRoundTime - 1)

        is GameHostEvent.SendMaxRoundTime -> copy(maxRoundTime = event.maxRoundTime)

        is GameHostEvent.SendCard -> copy(currentCard = event.card)

        is GameHostEvent.PlayerJoined -> addPlayer(event.player)

        is GameHostEvent.SendMatch -> copy(match = event.match)

        is GameHostEvent.StartRound -> TODO()

        is GameHostEvent.EndGame -> TODO()

        is GameHostEvent.PlayerJoinedTeam -> {
            movePlayerToTeam(player = event.player, team = event.team)
        }

        is GameHostEvent.PlayerLeft -> removePlayer(event.player)

        is GameHostEvent.YouJoined -> copy(me = event.player)

        is GameHostEvent.StartGame -> copy(match = event.match, phase = GamePhase.PLAYING)
    }

    fun addPlayer(player: Player) =
        this.copy(match = this.match?.copy(players = this.match.players + player))

    fun removePlayer(player: Player): GameState {
        val currentMatch = match ?: return this

        val updatedTeams = currentMatch.teams.map { team ->
            team.copy(players = team.players - player)
        }

        return copy(
            match = currentMatch.copy(
                players = currentMatch.players - player,
                teams = updatedTeams )
        )
    }

    fun movePlayerToTeam(player: Player, team: Team): GameState {
        val currentMatch = match ?: return this

        val updatedTeams = currentMatch.teams.map { existingTeam ->
            when {
                existingTeam == team -> existingTeam.copy(players = (existingTeam.players - player) + player)
                player in existingTeam.players -> existingTeam.copy(players = existingTeam.players - player)
                else -> existingTeam
            }
        }

        return copy(match = currentMatch.copy(teams = updatedTeams))
    }

    fun getPlayer(id: String): Player? = match?.players?.find { it.id == id }
}

