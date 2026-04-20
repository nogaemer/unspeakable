package de.nogaemer.unspeakable.features.settings.pages.personalization

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.push
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.core.components.menu.MenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.features.settings.SettingsOverviewComponent
import de.nogaemer.unspeakable.features.settings.pages.personalization.palette_style.DefaultPaletteStyleComponent
import de.nogaemer.unspeakable.features.settings.pages.personalization.palette_style.PaletteStyleScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class PersonalizationSettingsConfig {
    @Serializable
    data object Overview : PersonalizationSettingsConfig()

    @Serializable
    data object PaletteStyle : PersonalizationSettingsConfig()
}


class DefaultPersonalizationComponent(
    ctx: ComponentContext,
    override val onBack: () -> Unit,
) : DefaultMenuComponent<PersonalizationSettingsConfig, SettingsOverviewComponent<PersonalizationSettingsConfig>>(
    ctx = ctx,
    serializer = PersonalizationSettingsConfig.serializer(),
    initialConfig = PersonalizationSettingsConfig.Overview,
    childFactory = { config, childCtx, push, pop, _ ->
        when (config) {
            is PersonalizationSettingsConfig.Overview -> MenuChild.Overview(
                SettingsOverviewComponent(
                    ctx = childCtx,
                    goBack = onBack,
                    onNavigate = push,
                    items = listOf()
                )
            )

            is PersonalizationSettingsConfig.PaletteStyle -> {
                val c = DefaultPaletteStyleComponent(childCtx, pop)
                MenuChild.Page(c) { PaletteStyleScreen(c) }
            }
        }
    }
), MenuPage {

    companion object {
        val titleKey = { s: Strings -> s.settings.personalizationStrings.title }
        val descriptionKey = { s: Strings -> s.settings.personalizationStrings.description }
        val icon = Lucide.Palette
    }

    override val titleKey = Companion.titleKey
    override val descriptionKey = Companion.descriptionKey
    override val icon = Companion.icon

    @OptIn(DelicateDecomposeApi::class)
    fun onNavigate(config: PersonalizationSettingsConfig) = navigation.push(config)

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

