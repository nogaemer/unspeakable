package de.nogaemer.unspeakable.core.model

import kotlinx.serialization.Serializable

@Serializable
sealed class GameConfig {

    @Serializable
    data class Local(
        val playerName: String,
        val profilePicture: ProfilePicture,
        val roundTimeSeconds: Int = 60,
    ) : GameConfig()

    @Serializable
    data class Host(
        val playerName: String,
        val profilePicture: ProfilePicture,
        val roundTimeSeconds: Int = 60,
    ) : GameConfig()

    @Serializable
    data class Join(
        val playerName: String,
        val profilePicture: ProfilePicture,
        val hostIp: String,
        val roundTimeSeconds: Int = 60,
    ) : GameConfig()
}
