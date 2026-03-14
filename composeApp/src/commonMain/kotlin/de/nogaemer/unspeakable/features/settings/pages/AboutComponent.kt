package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.features.settings.SettingsPage

interface AboutComponent: SettingsPage {
    fun onBack()
}

class DefaultAboutComponent(
    ctx: ComponentContext,
    private val _onBack: () -> Unit
): AboutComponent {
    override fun onBack() = _onBack()

    override val titleKey:  ((Strings) -> String) = { it.settings.aboutStrings.title }
    override val icon       = Lucide.Info
}