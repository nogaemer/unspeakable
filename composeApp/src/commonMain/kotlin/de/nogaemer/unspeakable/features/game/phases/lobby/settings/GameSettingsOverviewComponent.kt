package de.nogaemer.unspeakable.features.game.phases.lobby.settings

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.components.menu.OverviewItem
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.GameSettingsComponent
import kotlinx.coroutines.flow.StateFlow

/**
 * Binds lobby settings state/events to overview navigation actions.
 */
class GameSettingsOverviewComponent<Config : Any>(
    ctx: ComponentContext,
    override val state: StateFlow<GameState>,
    override val onEvent: (GameClientEvent) -> Unit,

    private val onNavigate: (Config) -> Unit,
    val goBack: () -> Unit,
    val items: List<List<OverviewItem<Config>>>,
) : GameSettingsComponent, ComponentContext by ctx {

    fun navigateTo(config: Config) = onNavigate(config)
}