package de.nogaemer.unspeakable.features.game.phases.lobby

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes shared state and events for lobby settings subcomponents.
 */
interface GameSettingsComponent : ComponentContext {
    val state: StateFlow<GameState>
    val onEvent: (GameClientEvent) -> Unit
}