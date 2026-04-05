package de.nogaemer.unspeakable.core.mode

import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.PlayedCard
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.db.UnspeakableCard

/**
 * Represents a chain of game modes that can be composed together.
 */
class ModeChain(modes: List<GameMode>) {

    // Pair each mode with its own mutable state
    private val chain: List<Pair<GameMode, ModeState>> =
        modes.map { it to it.createState() }

    suspend fun onRoundInit(round: Round): List<GameHostEvent> =
        chain.flatMap { (mode, state) -> mode.onRoundInit(round, state) }

    suspend fun onRoundStart(): List<GameHostEvent> =
        chain.flatMap { (mode, state) -> mode.onRoundStart(state) }

    suspend fun onRoundEnd(round: Round): List<GameHostEvent> =
        chain.flatMap { (mode, state) -> mode.onRoundEnd(round, state) }

    suspend fun onTick(remainingTime: Int): Pair<Boolean, List<GameHostEvent>> {
        for ((mode, state) in chain) {
            val result = mode.onTick(remainingTime, state)
            if (result.consumed) return true to result.extraEvents
        }
        return false to emptyList() // no mode consumed it — default tick applies
    }

    /**
     * Asks each mode if it wants to override the round start time.
     * The first non-null answer wins (only one mode can own TIMER_CONTROL).
     */
    fun resolveRoundStartTime(defaultTime: Int): Int {
        for ((mode, state) in chain) {
            val override = mode.onResolveStartTime(defaultTime, state)
            if (override != null) return override
        }
        return defaultTime
    }

    /**
     * Handles the event of a played card and processes it through each mode in the chain.
     * This method allows modes to intercept the event, injecting additional events or modifying the
     * behavior based on their logic.
     *
     * @param playedCard The card that was played with its associated outcome.
     * @return An `InterceptResult` containing any additional events, a possible override for the time delta,
     *         and whether the event was consumed by any mode.
     */
    suspend fun onCardPlayed(playedCard: PlayedCard): InterceptResult  {
        val extra = mutableListOf<GameHostEvent>()
        var totalDelta = 0
        for ((mode, state) in chain) {
            val result = mode.onCardPlayed(playedCard, state)
            extra += result.extraEvents
            result.timeDelta?.let { totalDelta += it }
            if (result.consumed) break
        }
        return InterceptResult(
            extraEvents = extra,
            timeDelta = if (totalDelta != 0) totalDelta else null,
        )

    }

    /**
     * Handles the sabotage event triggered by the opposing team. This method iterates through the chain of modes,
     * invoking their respective `onSabotage` method to determine how the sabotage is processed, including generating
     * additional game events if applicable.
     *
     * @param buzzer The player who initiated the sabotage event.
     * @param sabotageWord An optional string representing the word associated with the sabotage, or null if not applicable.
     * @return A list of `GameHostEvent` instances generated as a result of the sabotage processing.
     */
    suspend fun onSabotage(buzzer: Player, sabotageWord: String? = null): List<GameHostEvent> {
        val extra = mutableListOf<GameHostEvent>()
        for ((mode, state) in chain) {
            val result = mode.onSabotage(buzzer, state, sabotageWord)
            extra += result.extraEvents
            if (result.consumed) break
        }
        return extra
    }

    fun onCardDealt(card: UnspeakableCard): UnspeakableCard =
        chain.fold(card) { current, (mode, state) -> mode.onCardDealt(current, state) }

    @Suppress("UNCHECKED_CAST")
    fun <T : ModeState> getState(modeId: String): T? =
        chain.firstOrNull { (mode, _) -> mode.id == modeId }?.second as? T

    fun hasMode(modeId: String): Boolean =
        chain.any { (mode, _) -> mode.id == modeId }
}