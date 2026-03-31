package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.core.components.menu.OverviewItem
import de.nogaemer.unspeakable.features.settings.SettingsConfig

interface SettingsOverviewComponent {
    val items: List<List<OverviewItem<SettingsConfig>>>
    fun navigateTo(config: SettingsConfig)
}

class DefaultSettingsOverviewComponent(
    ctx: ComponentContext,
    private val onNavigate: (SettingsConfig) -> Unit,
) : SettingsOverviewComponent, ComponentContext by ctx {

    override val items: List<List<OverviewItem<SettingsConfig>>> = listOf(
        listOf(
            OverviewItem(
                config = SettingsConfig.Personalization,
                icon = Lucide.Palette,
                titleKey = { it.settings.personalizationStrings.title },
                descriptionKey = { it.settings.personalizationStrings.description },
            ),
            OverviewItem(
                config = SettingsConfig.Language,
                icon = Lucide.Globe,
                titleKey = { it.settings.languageStrings.title },
                descriptionKey = { it.settings.languageStrings.description },
            ),
        ),
        listOf(
            OverviewItem(
                config = SettingsConfig.About,
                icon = Lucide.Info,
                titleKey = { it.settings.aboutStrings.title },
                descriptionKey = null,
            ),
        ),
    )

    override fun navigateTo(config: SettingsConfig) = onNavigate(config)
}
