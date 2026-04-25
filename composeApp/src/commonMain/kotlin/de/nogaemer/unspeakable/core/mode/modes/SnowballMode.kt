package de.nogaemer.unspeakable.core.mode.modes

import de.nogaemer.unspeakable.core.mode.Capability
import de.nogaemer.unspeakable.core.mode.GameMode
import de.nogaemer.unspeakable.core.mode.InterceptResult
import de.nogaemer.unspeakable.core.mode.ModeState
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.PlayedCard
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.features.game.GameState

/**
 * Game mode where taboo words accumulate during a round.
 */
class SnowballMode : GameMode() {
    companion object {
        const val id = "snowball"
    }
    override val id = Companion.id


    override val exclusiveCapabilities = setOf(Capability.TURN_ACCUMULATION)

    override fun createState() = ModeState.Snowball()

    override suspend fun onRoundInit(round: Round, modeState: ModeState): List<GameHostEvent> {
        (modeState as ModeState.Snowball).contaminatedWords.clear()
        return listOf(GameHostEvent.ContaminationUpdated(emptyList()))
    }

    override suspend fun onCardPlayed(playedCard: PlayedCard, modeState: ModeState, gameState: GameState): InterceptResult {
        val s = modeState as ModeState.Snowball
        if (playedCard.outcome == CardOutcome.CORRECT) {
            s.contaminatedWords.add(playedCard.card.word.lowercase())
        }
        return InterceptResult(
            extraEvents = listOf(GameHostEvent.ContaminationUpdated(s.contaminatedWords.toList()))
        )
    }

    // Inject previously guessed words as extra taboo words on every new card
    override fun onCardDealt(card: UnspeakableCard, modeState: ModeState): UnspeakableCard {
        val s = modeState as ModeState.Snowball
        return card.copy(
            forbiddenWords = (card.forbiddenWords + s.contaminatedWords).distinct()
        )
    }
}