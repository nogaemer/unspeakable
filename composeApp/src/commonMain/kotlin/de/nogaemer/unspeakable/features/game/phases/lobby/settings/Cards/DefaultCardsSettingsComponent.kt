package de.nogaemer.unspeakable.features.game.phases.lobby.settings.cards

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.GameSettingsComponent
import kotlinx.coroutines.flow.StateFlow

class DefaultCardsSettingsComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
    override val state: StateFlow<GameState>,
    override val onEvent: (GameClientEvent) -> Unit,
) : AbstractMenuPage(ctx, onBack), GameSettingsComponent {
    override val titleKey: (Strings) -> String = { it.gameLobbySettings.categoriesSettingsStrings.title }
    override val descriptionKey: (Strings) -> String = { it.gameLobbySettings.categoriesSettingsStrings.description }
    override val icon = Lucide.BookOpen
}

