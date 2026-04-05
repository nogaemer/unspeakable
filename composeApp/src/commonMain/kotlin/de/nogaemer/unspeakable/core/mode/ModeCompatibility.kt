package de.nogaemer.unspeakable.core.mode

import kotlinx.serialization.Serializable

sealed class CompatibilityResult {
    object Compatible : CompatibilityResult()
    data class Incompatible(val conflicts: List<CapabilityConflict>) : CompatibilityResult()
    data class SoftWarning(val warnings: List<SoftConflictWarning>) : CompatibilityResult()
}

data class CapabilityConflict(
    val capability: Capability,
    val modeA: String,
    val modeB: String,
) {
    override fun toString() =
        "'$modeA' and '$modeB' both claim $capability — only one may be active."
}

@Serializable
data class SoftConflictWarning(val modeA: String, val modeB: String)

/**
 * Describes compatibility rules between different game modes.
 */
object ModeCompatibility {

    fun check(modes: List<GameMode>): CompatibilityResult {
        val hardConflicts = detectCapabilityConflicts(modes)
        if (hardConflicts.isNotEmpty()) return CompatibilityResult.Incompatible(hardConflicts)

        val softWarnings = detectSoftConflicts(modes)
        if (softWarnings.isNotEmpty()) return CompatibilityResult.SoftWarning(softWarnings)

        return CompatibilityResult.Compatible
    }

    private fun detectCapabilityConflicts(modes: List<GameMode>): List<CapabilityConflict> {
        // Map each capability to all modes that claim it
        val claims = mutableMapOf<Capability, MutableList<String>>()
        for (mode in modes) {
            for (cap in mode.exclusiveCapabilities) {
                claims.getOrPut(cap) { mutableListOf() }.add(mode.id)
            }
        }
        // Any capability claimed by 2+ modes is a conflict
        return claims
            .filter { (_, claimants) -> claimants.size > 1 }
            .flatMap { (cap, claimants) ->
                claimants.zipWithNext { a, b -> CapabilityConflict(cap, a, b) }
            }
    }

    private fun detectSoftConflicts(modes: List<GameMode>): List<SoftConflictWarning> {
        val modeIds = modes.map { it.id }.toSet()
        return modes.flatMap { mode ->
            mode.softConflicts
                .filter { it in modeIds }
                .map { conflictId -> SoftConflictWarning(mode.id, conflictId) }
        }
    }
}