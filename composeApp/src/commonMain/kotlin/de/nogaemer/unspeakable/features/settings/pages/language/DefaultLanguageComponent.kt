package de.nogaemer.unspeakable.features.settings.pages.language

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.util.settings.Locales

class DefaultLanguageComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) : AbstractMenuPage(ctx, onBack) {

    override val titleKey: (Strings) -> String = { it.settings.languageStrings.title }
    override val descriptionKey: (Strings) -> String = { it.settings.languageStrings.description }
    override val icon = Lucide.Globe

    fun onLanguageSelected(language: Locales) {
        // Intentionally empty: selection is persisted in LanguageScreen via LocalAppSettings.
    }
}

