package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings

class DefaultAboutComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) : AbstractMenuPage(ctx, onBack) {

    override val titleKey: (Strings) -> String = { it.settings.aboutStrings.title }
    override val descriptionKey: ((Strings) -> String)? = null
    override val icon = Lucide.Info
}


