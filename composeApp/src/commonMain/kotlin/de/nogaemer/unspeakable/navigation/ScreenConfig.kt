package de.nogaemer.unspeakable.navigation

import de.nogaemer.unspeakable.core.model.GameConfig
import de.nogaemer.unspeakable.features.game_settings.NetworkMode
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenConfig {
    @Serializable
    data object Main : ScreenConfig()

    @Serializable
    data class Setup(val networkMode: NetworkMode) : ScreenConfig()

    @Serializable
    data class Game(val gameConfig: GameConfig) : ScreenConfig()
}
