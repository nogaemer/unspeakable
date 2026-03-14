package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.features.settings.SettingsPage

interface PersonalizationComponent : SettingsPage {
    fun onBack()
    fun toggleDarkMode(isDark: Boolean, darkMode: Boolean? = null)
    fun toggleUseDynamicColor(useDynamicColor: Boolean? = null)
}

class DefaultPersonalizationComponent(
    ctx: ComponentContext,
    private val _onBack: () -> Unit,
) : PersonalizationComponent {

    override val titleKey: ((Strings) -> String) = { it.settings.personalizationStrings.title }
    override val descriptionKey: ((Strings) -> String) =
        { it.settings.personalizationStrings.description }
    override val icon = Lucide.Palette

    override fun onBack() = _onBack()

    override fun toggleDarkMode(isDark: Boolean, darkMode: Boolean?) {
        if (!isDark) return
        Graph.settings.setAmoled(darkMode ?: !Graph.settings.appSettings.isAmoled)
    }

    override fun toggleUseDynamicColor(useDynamicColor: Boolean?) {
        Graph.settings.setDynamicColor(
            useDynamicColor ?: !Graph.settings.appSettings.useDynamicColor
        )
    }
}