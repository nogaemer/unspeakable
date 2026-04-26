package de.nogaemer.unspeakable.core.mode.modes

import de.nogaemer.unspeakable.core.mode.Capability
import de.nogaemer.unspeakable.core.mode.GameMode
import de.nogaemer.unspeakable.core.mode.InterceptResult
import de.nogaemer.unspeakable.core.mode.ModeState
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.db.UnspeakableCard

/**
 * Game mode where players can sabotage each other by injecting words.
 */
class SabotageMode : GameMode() {
    companion object {
        const val id = "sabotage"
    }
    override val id = Companion.id

    override val exclusiveCapabilities = setOf(Capability.CARD_MUTATION)

    override fun createState() = ModeState.Sabotage()

    /**
     * Called when a player buzzes in. If a sabotage word is provided,
     * injects it into the mode state and emits a SabotageUsed event.
     */
    override suspend fun onSabotage(
        buzzer: Player,
        modeState: ModeState,
        sabotageWord: String?,
    ): InterceptResult {
        val s = modeState as ModeState.Sabotage
        val word = sabotageWord ?: return InterceptResult()
        if (word.isEmpty()) return InterceptResult()

        // Hard once-per-round-per-team limit
        if (buzzer.teamId in s.usedByTeamIds) {
            return InterceptResult(
                consumed = true,
                extraEvents = listOf(GameHostEvent.SabotageDenied(buzzer))
            )
        }

        s.usedByTeamIds.add(buzzer.teamId)
        s.injectedWords.add(word)

        return InterceptResult(
            consumed = true,
            extraEvents = listOf(
                GameHostEvent.SabotageUsed(
                    byPlayer = buzzer,
                    newTabooWord = word,
                )
            )
        )
    }

    // Called every time a new card is dealt — injects all sabotage words
    override fun onCardDealt(card: UnspeakableCard, modeState: ModeState): UnspeakableCard {
        val s = modeState as ModeState.Sabotage
        if (s.injectedWords.isEmpty()) return card
        return card.copy(forbiddenWords = card.forbiddenWords + s.injectedWords)
    }

    // Reset injected words when the round ends
    override suspend fun onRoundEnd(round: Round, modeState: ModeState): List<GameHostEvent> {
        (modeState as ModeState.Sabotage).reset()
        return emptyList()
    }
}