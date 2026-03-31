package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph

class DefaultPersonalizationComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) : AbstractMenuPage(ctx, onBack) {

    override val titleKey: (Strings) -> String = { it.settings.personalizationStrings.title }
    override val descriptionKey: (Strings) -> String =
        { it.settings.personalizationStrings.description }
    override val icon = Lucide.Palette

    fun toggleDarkMode(isDark: Boolean, darkMode: Boolean? = null) {
        if (!isDark) return
        Graph.settings.setAmoled(darkMode ?: !Graph.settings.appSettings.isAmoled)
    }

    fun toggleUseDynamicColor(useDynamicColor: Boolean? = null) {
        Graph.settings.setDynamicColor(
            useDynamicColor ?: !Graph.settings.appSettings.useDynamicColor
        )
    }
}

