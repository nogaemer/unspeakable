package de.nogaemer.unspeakable.features.game.phases.lobby.settings.modes

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wrench
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.GameSettingsComponent
import kotlinx.coroutines.flow.StateFlow

/**
 * Default UI component for displaying and editing a single game mode's settings.
 */
class DefaultModeComponent(
    ctx: ComponentContext, onBack: () -> Unit,
    override val state: StateFlow<GameState>,
    override val onEvent: (GameClientEvent) -> Unit,
) : AbstractMenuPage(ctx, onBack), GameSettingsComponent {
    override val titleKey =
        { s: Strings -> s.gameLobbySettings.gameModeSettingsStrings.title }
    override val icon = Lucide.Wrench
}
