package de.nogaemer.unspeakable.features.game.phases.lobby.settings.round

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Timer
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.core.components.menu.MenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.GameSettingsComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.GameRoundConfig
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.GameSettingsOverviewComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.round.custom_round_count.CustomRoundCountScreen
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.round.custom_round_count.DefaultCustomRoundCountComponent
import kotlinx.coroutines.flow.StateFlow


/**
 * Hosts navigation for predefined and custom round-count settings.
 */
class DefaultRoundCountComponent(
    ctx: ComponentContext,
    override val onBack: () -> Unit,
    override val state: StateFlow<GameState>,
    override val onEvent: (GameClientEvent) -> Unit,
) : DefaultMenuComponent<GameRoundConfig, GameSettingsOverviewComponent<GameRoundConfig>>(
    ctx           = ctx,
    serializer    = GameRoundConfig.serializer(),
    initialConfig = GameRoundConfig.Overview,
    childFactory  = { config, childCtx, push, pop, _ ->
        when (config) {
            is GameRoundConfig.Overview -> MenuChild.Overview(
                GameSettingsOverviewComponent(
                    ctx = childCtx,
                    state = state,
                    onEvent = onEvent,
                    goBack = onBack,
                    onNavigate = push,
                    items = listOf()
                )
            )
            is GameRoundConfig.CustomRoundSettings -> {
                val c = DefaultCustomRoundCountComponent(childCtx, pop, state, onEvent)
                MenuChild.Page(c) { CustomRoundCountScreen(c) }
            }
        }
    }
), GameSettingsComponent, MenuPage {
    override val titleKey = { s: Strings -> s.gameLobbySettings.roundsSettings }
    override val icon = Lucide.Timer
}
