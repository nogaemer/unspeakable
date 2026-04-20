package de.nogaemer.unspeakable.features.game.phases.lobby.settings

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.core.components.menu.NestedMenuScreen
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.GameSettingsComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.cards.CardsSettingsScreen
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.cards.DefaultCardsSettingsComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.modes.DefaultModeComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.modes.DefaultModeScreen
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.round.DefaultRoundCountComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.round.RoundCountOverviewScreen
import kotlinx.coroutines.flow.StateFlow

/**
 * Default UI component for displaying and editing all game settings.
 *
 * Hosts nested navigation for lobby settings and round-count pages.
 */
class DefaultGameSettingsComponent(
    ctx: ComponentContext,
    override val state: StateFlow<GameState>,
    override val onEvent: (GameClientEvent) -> Unit,
    val goBack: () -> Unit
) : DefaultMenuComponent<GameSettingsConfig, GameSettingsOverviewComponent<GameSettingsConfig>>(
    ctx = ctx,
    serializer = GameSettingsConfig.serializer(),
    initialConfig = GameSettingsConfig.Overview,
    childFactory = { config, childCtx, push, pop, _ ->
        when (config) {
            is GameSettingsConfig.Overview -> MenuChild.Overview(
                GameSettingsOverviewComponent(
                    ctx = childCtx,
                    state = state,
                    onEvent = onEvent,
                    goBack = goBack,
                    onNavigate = push,
                    items = listOf()
                )
            )

            is GameSettingsConfig.RoundCount -> {
                val c = DefaultRoundCountComponent(childCtx, pop, state, onEvent)
                MenuChild.SubMenu(c) {
                    NestedMenuScreen(
                        component          = c,
                        showBackOnOverview = true,
                        onRootBack         = c.onBack,
                        overviewContent    = { RoundCountOverviewScreen(it) }
                    )
                }
            }

            is GameSettingsConfig.ModeSettings -> {
                val c = DefaultModeComponent(childCtx, pop, state, onEvent)
                MenuChild.Page(c) { DefaultModeScreen(c) }
            }

            is GameSettingsConfig.CardsSettings -> {
                val c = DefaultCardsSettingsComponent(childCtx, pop, state, onEvent)
                MenuChild.Page(c) { CardsSettingsScreen(c) }
            }
        }
    }
), GameSettingsComponent