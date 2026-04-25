package de.nogaemer.unspeakable.features.game

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.core.model.GameRole
import de.nogaemer.unspeakable.core.model.Match
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.core.model.Team
import de.nogaemer.unspeakable.db.UnspeakableCard

/**
 * Represents current client-visible game state derived from host events.
 */
data class GameState(
    val isHost: Boolean = false,
    val hostIp: String? = null,
    val isLocalGame: Boolean = false,

    val phase: GamePhase = GamePhase.SETUP,
    val role: GameRole? = null,
    val me: Player? = null,

    val match: Match? = null,
    val currentRound: Round? = null,
    val currentCard: UnspeakableCard? = null,
    val currentRoundTime: Int? = null,
    val violatedForbiddenWord: String? = null,
    val violatedForbiddenWordDurationMs: Long = 0L,

    val lastSabotage: GameHostEvent.SabotageUsed? = null,
    val contaminatedWords: List<String> = emptyList(),

    val rounds: List<Round> = emptyList(),

) {
    val currentRoundNumber: Int get() = (rounds.size + 1)
    val currentExplainer: Player? get() = currentRound?.explainerPlayer
    val currentExplainerTeam: Team? get() = currentRound?.explainerTeam


    fun applyEvent(event: GameHostEvent): GameState = when (event) {

        is GameHostEvent.Tick ->
            copy(currentRoundTime = event.currentRoundTime)

        is GameHostEvent.SetRoundTime ->
            copy(currentRound = currentRound?.copy(roundTime =  event.roundTime))

        is GameHostEvent.SendCard ->
            copy(
                currentCard = event.card,
                violatedForbiddenWord = null,
                violatedForbiddenWordDurationMs = 0L,
            )

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

        is GameHostEvent.SendRound ->
            copy(currentRound = event.round)

        is GameHostEvent.SendPhase ->
            copy(phase = event.phase)

        is GameHostEvent.StartGame ->
            copy(match = event.match, phase = GamePhase.PLAYING)

        is GameHostEvent.InitNewRound ->
            copy(
                currentRound = event.round,
                currentCard = null,
                currentRoundTime = match?.settings?.roundTime,
                violatedForbiddenWord = null,
                violatedForbiddenWordDurationMs = 0L,
                phase = GamePhase.READY,
                role = event.role,
                lastSabotage = null,
            )

        is GameHostEvent.ForbiddenWordViolated ->
            copy(
                violatedForbiddenWord = event.word,
                violatedForbiddenWordDurationMs = event.durationMs,
            )

        is GameHostEvent.CardPlayed -> {
            val updated = currentRound?.copy(
                playedCards = currentRound.playedCards + event.playedCard
            )
            Logger.d { "Card played: ${updated?.playedCards}" }
            copy(currentRound = updated)
        }

        is GameHostEvent.EndRound ->
            copy(
                rounds = rounds + event.completedRound,
                match = match?.copy(teams = event.updatedTeams),
                violatedForbiddenWord = null,
                violatedForbiddenWordDurationMs = 0L,
                phase = GamePhase.ROUND_SUMMARY,
            )

        is GameHostEvent.EndGame ->
            copy(
                phase = GamePhase.GAME_OVER,
                violatedForbiddenWord = null,
                violatedForbiddenWordDurationMs = 0L,
            )

        is GameHostEvent.StartRound ->
            copy(phase = GamePhase.PLAYING)

        is GameHostEvent.SabotageDenied -> {
            this
        } //TODO

        is GameHostEvent.SabotageUsed -> {
            val updatedCard = currentCard?.copy(
                forbiddenWords = currentCard.forbiddenWords + event.newTabooWord
            )
            copy(
                currentCard = updatedCard,
                lastSabotage = event,
            )
        }

        is GameHostEvent.ContaminationUpdated ->
            copy(contaminatedWords = event.contaminatedWords)

        is GameHostEvent.ModeConflict -> {
            this
        } //TODO
        is GameHostEvent.ModeWarning -> {
            this
        } //TODO
    }


    fun addPlayer(player: Player) =
        this.copy(
            match = this.match?.copy(
                players = this.match.players
                    .filterNot { it.id == player.id } + player
            )
        )

    fun removePlayer(player: Player): GameState {
        val currentMatch = match ?: return this

        val updatedTeams = currentMatch.teams.map { team ->
            team.copy(players = team.players.filterNot { it.id == player.id })
        }

        return copy(
            match = currentMatch.copy(
                players = currentMatch.players.filterNot { it.id == player.id },
                teams = updatedTeams
            )
        )
    }

    fun movePlayerToTeam(player: Player, team: Team): GameState {
        val currentMatch = match ?: return this

        val updatedTeams = currentMatch.teams.map { existingTeam ->
            when {
                existingTeam.players.any { it.id == player.id } -> {
                    existingTeam.copy(players = existingTeam.players.filterNot { it.id == player.id })
                }

                existingTeam.id == team.id -> {
                    existingTeam.copy(
                        players = existingTeam.players
                            .filterNot { it.id == player.id } + player
                    )
                }

                else -> existingTeam
            }
        }

        val updatedPlayer = player.copy(teamId = team.id)
        val updatedPlayers = currentMatch.players.filterNot { it.id == player.id } + updatedPlayer
        val updatedMe = if (me?.id == player.id) updatedPlayer else me

        return copy(
            match = currentMatch.copy(
                teams = updatedTeams,
                players = updatedPlayers,
            ),
            me = updatedMe
        )
    }

    fun getPlayer(id: String): Player? = match?.players?.find { it.id == id }

    fun getTeam(id: String): Team? = match?.teams?.find { it.id == id }

    fun getTeamsWithoutCurrent(): List<Team> =
        match?.teams?.filter { it.id != currentExplainerTeam?.id } ?: emptyList()

    fun getTeamByPlayer(player: Player): Team? =
        match?.teams?.find { team -> team.players.any { it.id == player.id } }

    fun getTeamByPlayerId(playerId: String): Team? =
        getPlayer(playerId)?.let { getTeamByPlayer(it) }

}

