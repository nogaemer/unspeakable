package de.nogaemer.unspeakable.features.settings.pages.personalization.palette_style

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SwatchBook
import com.materialkolor.PaletteStyle
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph

class DefaultPaletteStyleComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) : AbstractMenuPage(ctx, onBack) {
    companion object {
        val titleKey = { s: Strings -> s.settings.personalizationStrings.paletteStyleStrings.title }
        val descriptionKey = { s: Strings -> s.settings.personalizationStrings.paletteStyleStrings.description }
        val icon = Lucide.SwatchBook
    }

    override val titleKey = Companion.titleKey
    override val descriptionKey = Companion.descriptionKey
    override val icon = Companion.icon

    fun selectPaletteStyle(paletteStyle: PaletteStyle) {
        val settings = Graph.settings
        val selected = Graph.settings.appSettings.paletteStyle
        if (selected != paletteStyle) {
            settings.setPaletteStyle(paletteStyle)
        }
    }

    fun isSelected(paletteStyle: PaletteStyle) = paletteStyle == Graph.settings.appSettings.paletteStyle

    val paletteStyles = PaletteStyle.entries.toTypedArray()
}
