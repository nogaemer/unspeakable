package de.nogaemer.unspeakable.features.settings

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.core.components.menu.NestedMenuScreen
import de.nogaemer.unspeakable.core.components.menu.OverviewItem
import de.nogaemer.unspeakable.features.settings.pages.about.AboutScreen
import de.nogaemer.unspeakable.features.settings.pages.about.DefaultAboutComponent
import de.nogaemer.unspeakable.features.settings.pages.language.DefaultLanguageComponent
import de.nogaemer.unspeakable.features.settings.pages.language.LanguageScreen
import de.nogaemer.unspeakable.features.settings.pages.personalization.DefaultPersonalizationComponent
import de.nogaemer.unspeakable.features.settings.pages.personalization.PersonalizationScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsConfig {
    @Serializable
    data object Overview : SettingsConfig()

    @Serializable
    data object Personalization : SettingsConfig()

    @Serializable
    data object Language : SettingsConfig()

    @Serializable
    data object About : SettingsConfig()
}

class DefaultSettingsComponent(
    ctx: ComponentContext,
) : DefaultMenuComponent<SettingsConfig, SettingsOverviewComponent<SettingsConfig>>(
    ctx = ctx,
    serializer = SettingsConfig.serializer(),
    initialConfig = SettingsConfig.Overview,
    childFactory = { config, childCtx, push, pop ->
        when (config) {
            is SettingsConfig.Overview -> MenuChild.Overview(
                SettingsOverviewComponent(
                    ctx = childCtx,
                    onNavigate = push,
                    goBack = pop,
                    items = listOf(
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
                )
            )

            is SettingsConfig.Personalization -> {
                val c = DefaultPersonalizationComponent(
                    childCtx,
                    onBack = pop,
                )
                MenuChild.SubMenu(c) {
                    NestedMenuScreen(
                        component = c,
                        showBackOnOverview = true,
                        onRootBack = c.onBack,
                        overviewContent = { PersonalizationScreen(c) }
                    )
                }
            }

            is SettingsConfig.Language -> {
                val component = DefaultLanguageComponent(childCtx, pop)
                MenuChild.Page(component) {
                    LanguageScreen(component)
                }
            }

            is SettingsConfig.About -> {
                val component = DefaultAboutComponent(childCtx, pop)
                MenuChild.Page(component) {
                    AboutScreen(component)
                }
            }
        }
    },
) {

    fun resetToOverview() {
        resetToRoot()
    }
}
