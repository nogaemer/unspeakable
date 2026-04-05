package de.nogaemer.unspeakable.core.mode

import co.touchlab.kermit.Logger
import de.nogaemer.unspeakable.core.mode.modes.SabotageMode
import de.nogaemer.unspeakable.core.mode.modes.SnowballMode
import de.nogaemer.unspeakable.core.mode.modes.SurvivalMode

/**
 * Registers and manages all available game modes.
 */
object ModeRegistry {

    /** All registered modes, keyed by their stable [GameMode.id]. */
    private val registry: Map<String, () -> GameMode> = mapOf(
        "sabotage"            to { SabotageMode() },
        "snowball"            to { SnowballMode() },
        "survival"            to { SurvivalMode() },
    )

    /** All modes available for display in the lobby settings UI. */
    val allModes: List<GameMode> get() = registry.values.map { it() }

    /**
     * Resolves a set of mode IDs into instantiated [GameMode] objects.
     *
     * - Unknown IDs are logged and silently skipped (defensive against
     *   stale settings persisted from a previous app version).
     * - Each call returns fresh instances so mode state is never shared
     *   across game sessions.
     */
    fun resolvedModes(enabledIds: Set<String>): List<GameMode> {
        return enabledIds.mapNotNull { id ->
            val factory = registry[id]
            if (factory == null) {
                Logger.w { "ModeRegistry: unknown mode id '$id' — skipped" }
            }
            factory?.invoke()
        }
    }

    /** Convenience: does a mode with this ID exist in the registry? */
    fun isKnown(id: String): Boolean = id in registry
}