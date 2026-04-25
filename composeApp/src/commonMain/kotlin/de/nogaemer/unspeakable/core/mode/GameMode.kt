package de.nogaemer.unspeakable.core.mode


import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.model.GameHostEvent
import de.nogaemer.unspeakable.core.model.PlayedCard
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.features.game.GameState

/**
 * Result returned by a mode when it intercepts an event.
 *
 * @param consumed      Stop passing this event to further modes in the chain.
 * @param extraEvents   Additional GameHostEvents to broadcast after this one.
 * @param mutatedCard   If set, replaces the current card being described.
 * @param timeDelta     If set, overrides the default time.
 */
data class InterceptResult(
    val consumed: Boolean = false,
    val extraEvents: List<GameHostEvent> = emptyList(),
    val mutatedCard: UnspeakableCard? = null,
    val timeDelta: Int? = null,
)

/**
 * Base interface for all game modes, defining their behavior and capabilities.
 */
abstract class GameMode {
    /** Unique stable ID — used for persistence and conflict keys. */
    abstract val id: String

    /** Human-readable name shown in lobby settings. */
    open val displayName = { s: Strings -> s.game.gameModesStrings[id]!!.title }

    /** Human-readable description shown in lobby settings. */
    open val description = { s: Strings -> s.game.gameModesStrings[id]!!.description }

    /**
     * Exclusive capabilities this mode claims.
     * Any other active mode claiming the same capability → incompatible.
     */
    abstract val exclusiveCapabilities: Set<Capability>

    /**
     * Optional soft-conflict: compatible technically but bad UX together.
     * These produce warnings, not hard blocks.
     */
    open val softConflicts: Set<String> get() = emptySet()

    // ── Lifecycle hooks (all have no-op defaults) ──────────────────────

    /** Called once before the round starts. */
    open suspend fun onRoundInit(round: Round, modeState: ModeState): List<GameHostEvent> = emptyList()

    /** Called once when the round starts. Can inject setup events. */
    open suspend fun onRoundStart(modeState: ModeState): List<GameHostEvent> = emptyList()

    /** Called once when the round ends. */
    open suspend fun onRoundEnd(round: Round, modeState: ModeState): List<GameHostEvent> = emptyList()

    /**
     * Called on every timer tick.
     * Survival mode uses this to cancel the default tick and replace it.
     */
    open suspend fun onTick(remainingTime: Int, modeState: ModeState): InterceptResult = InterceptResult()

    /**
     * Called once before the Timer is created for a round.
     * Return a non-null value to override the default round time.
     * Only one mode should claim [Capability.TIMER_CONTROL].
     */
    open fun onResolveStartTime(defaultTime: Int, modeState: ModeState): Int? = null

    /**
     * Called when a card outcome is recorded (correct/skipped/wrong).
     * Modes can inject extra events (bonus points, timer changes, etc.)
     */
    open suspend fun onCardPlayed(playedCard: PlayedCard, modeState: ModeState, gameState: GameState): InterceptResult = InterceptResult()

    /**
     * Called when the opposing team presses buzz.
     * Bluff Cards mode uses this to check if it's a fake taboo word.
     */
    open suspend fun onSabotage(buzzer: Player, modeState: ModeState, sabotageWord: String?): InterceptResult = InterceptResult()

    /**
     * Called when a new card is dealt.
     * Modes can mutate the card (add/hide taboo words etc.)
     */
    open fun onCardDealt(card: UnspeakableCard, modeState: ModeState): UnspeakableCard = card

    /** Initial in-memory state for this mode instance. */
    open fun createState(): ModeState = ModeState.Empty
}

/** Per-mode mutable state, lives in GameAuthority — not serialized or broadcast. */
sealed class ModeState {
    object Empty : ModeState()
    data class Survival(var remainingTime: Int = 15) : ModeState()
    data class Snowball(val contaminatedWords: MutableSet<String> = mutableSetOf()) : ModeState()
    data class Sabotage(
        val usedByTeamIds: MutableSet<String> = mutableSetOf(),
        val injectedWords: MutableList<String> = mutableListOf(),
    ) : ModeState() {
        fun reset() { usedByTeamIds.clear(); injectedWords.clear() }
    }
}