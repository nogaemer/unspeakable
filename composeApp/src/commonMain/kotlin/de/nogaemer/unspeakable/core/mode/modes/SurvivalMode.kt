package de.nogaemer.unspeakable.core.mode.modes

import de.nogaemer.unspeakable.core.mode.Capability
import de.nogaemer.unspeakable.core.mode.GameMode
import de.nogaemer.unspeakable.core.mode.InterceptResult
import de.nogaemer.unspeakable.core.mode.ModeState
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.PlayedCard

/**
 * Game mode where teams must survive as long as possible under time pressure.
 */
class SurvivalMode : GameMode() {
    companion object {
        const val id = "survival"
    }
    override val id = Companion.id

    override val exclusiveCapabilities = setOf(Capability.TIMER_CONTROL)

    private val startTime = 15
    private val correctBonus = +5
    private val wrongPenalty = -5

    override fun createState() = ModeState.Survival(remainingTime = startTime)

    // Tell GameAuthority to create the Timer with 15s instead of roundTime
    override fun onResolveStartTime(defaultTime: Int, modeState: ModeState): Int = startTime

    // Return the delta — GameAuthority applies it to the real Timer
    override suspend fun onCardPlayed(
        playedCard: PlayedCard,
        modeState: ModeState,
    ): InterceptResult {
        val delta = when (playedCard.outcome) {
            CardOutcome.CORRECT -> correctBonus
            CardOutcome.WRONG   -> wrongPenalty
            CardOutcome.SKIPPED -> 0
        }
        return InterceptResult(timeDelta = delta)
    }

}