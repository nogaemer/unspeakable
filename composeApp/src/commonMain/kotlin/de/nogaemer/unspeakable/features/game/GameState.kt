package de.nogaemer.unspeakable.features.game

import co.touchlab.kermit.Logger
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
    val me: Player? = null,
    val match: Match? = null,

    // Active round tracking
    val currentRound: Round? = null,
    val currentCard: UnspeakableCard? = null,
    val currentRoundTime: Int? = null,

    // Completed rounds history
    val rounds: List<Round> = emptyList(),
) {
    val currentRoundNumber: Int get() = (rounds.size + 1)
    val currentExplainer: Player? get() = currentRound?.explainerPlayer
    val currentExplainerTeam: Team? get() = currentRound?.explainerTeam


    fun applyEvent(event: GameHostEvent): GameState = when (event) {

        is GameHostEvent.Tick ->
            copy(currentRoundTime = event.currentRoundTime)

        is GameHostEvent.SendCard ->
            copy(currentCard = event.card)

        is GameHostEvent.PlayerJoined ->
            addPlayer(event.player)

        is GameHostEvent.YouJoined ->
            copy(me = event.player)

        is GameHostEvent.PlayerLeft ->
            removePlayer(event.player)

        is GameHostEvent.PlayerJoinedTeam ->
            movePlayerToTeam(event.player, event.team)

        is GameHostEvent.SendMatch ->
            copy(match = event.match)

        is GameHostEvent.SendGameSettings ->
            copy(match = match?.copy(settings = event.settings))

        is GameHostEvent.StartGame ->
            copy(match = event.match, phase = GamePhase.PLAYING)

        is GameHostEvent.InitNewRound ->
            copy(
                currentRound = event.round,
                currentCard = null,
                currentRoundTime = match?.settings?.roundTime,
                phase = GamePhase.READY,
            )

        is GameHostEvent.CardPlayed -> {
            val updated = currentRound?.copy(
                playedCards = currentRound.playedCards + event.playedCard
            )
            Logger.d { "Card played: ${updated?.playedCards}" }
            copy(currentRound = updated, currentCard = null)
        }

        is GameHostEvent.EndRound ->
            copy(
                rounds = rounds + event.completedRound,
                phase = GamePhase.ROUND_SUMMARY,
            )

        is GameHostEvent.EndGame ->
            copy(phase = GamePhase.GAME_OVER)

        is GameHostEvent.StartRound ->
            copy(phase = GamePhase.PLAYING)
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
                teams = updatedTeams
            )
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

    fun getTeam(id: String): Team? = match?.teams?.find { it.id == id }

    fun getTeamsWithoutCurrent(): List<Team> =
        match?.teams?.filter { it.id != currentExplainerTeam?.id } ?: emptyList()

    fun getTeamByPlayer(player: Player): Team? = match?.teams?.find { player in it.players }

    fun getTeamByPlayerId(playerId: String): Team? =
        getPlayer(playerId)?.let { getTeamByPlayer(it) }

}

