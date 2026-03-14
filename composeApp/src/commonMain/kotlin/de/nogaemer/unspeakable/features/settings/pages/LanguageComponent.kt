package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.util.settings.Locales
import de.nogaemer.unspeakable.features.settings.SettingsPage

interface LanguageComponent : SettingsPage {
    fun onBack()
    fun onLanguageSelected(language: Locales)
}

class DefaultLanguageComponent(
    ctx: ComponentContext,
    private val _onBack: () -> Unit,
) : LanguageComponent {

    override val titleKey:          ((Strings) -> String) = { it.settings.languageStrings.title }
    override val descriptionKey:    ((Strings) -> String) = { it.settings.languageStrings.description }
    override val icon               = Lucide.Globe

    override fun onBack() = _onBack()
    override fun onLanguageSelected(language: Locales) {
//        TODO("Not yet implemented")
    }
}