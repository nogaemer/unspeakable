package de.nogaemer.unspeakable.features.game.phases.lobby.settings

import kotlinx.serialization.Serializable

/**
 * Defines navigation destinations for the lobby settings stack.
 */
@Serializable
sealed class GameSettingsConfig  {
    @Serializable data object Overview   : GameSettingsConfig ()
    @Serializable data object RoundCount   : GameSettingsConfig ()
}

/**
 * Defines destinations inside round-count settings.
 */
@Serializable
sealed class GameRoundConfig  {
    @Serializable data object Overview   : GameRoundConfig ()
    @Serializable data object CustomRoundSettings   : GameRoundConfig ()
}

