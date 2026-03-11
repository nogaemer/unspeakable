package de.nogaemer.unspeakable.core.model

import kotlinx.serialization.Serializable

@Serializable
sealed class GameConfig {

    @Serializable
    data object Local : GameConfig()

    @Serializable
    data class Host(
        val playerName: String,
        val roundTimeSeconds: Int = 60,
    ) : GameConfig()

    @Serializable
    data class Join(
        val playerName: String,
        val hostIp: String,
        val roundTimeSeconds: Int = 60,
    ) : GameConfig()
}
