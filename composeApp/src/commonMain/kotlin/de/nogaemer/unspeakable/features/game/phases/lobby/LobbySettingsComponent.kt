package de.nogaemer.unspeakable.features.game.phases.lobby

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.flow.StateFlow

interface LobbySettingsComponent : ComponentContext {
    val state: StateFlow<GameState>
    fun onEvent(event: GameClientEvent)
    fun goBack()
}

class DefaultLobbySettingsComponent(
    ctx: ComponentContext,
    override val state: StateFlow<GameState>,
    private val _onEvent: (GameClientEvent) -> Unit,
    private val _goBack: () -> Unit,
) : LobbySettingsComponent, ComponentContext by ctx {
    override fun onEvent(event: GameClientEvent) = _onEvent(event)
    override fun goBack() = _goBack()
}