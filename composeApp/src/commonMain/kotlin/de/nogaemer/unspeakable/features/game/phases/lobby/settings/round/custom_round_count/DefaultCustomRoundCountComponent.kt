package de.nogaemer.unspeakable.features.game.phases.lobby.settings.round.custom_round_count

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.GameSettingsComponent
import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes state/events for the custom round-count settings page.
 */
class DefaultCustomRoundCountComponent(
    ctx: ComponentContext, onBack: () -> Unit,
    override val state: StateFlow<GameState>,
    override val onEvent: (GameClientEvent) -> Unit,
) : AbstractMenuPage(ctx, onBack), GameSettingsComponent {
    override val titleKey = { s: Strings -> s.gameLobbySettings.gameRoundSettingsStrings.title }
    override val icon = Lucide.Clock
}
