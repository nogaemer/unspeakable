package de.nogaemer.unspeakable.features.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.features.settings.pages.AboutScreen
import de.nogaemer.unspeakable.features.settings.pages.DefaultAboutComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultLanguageComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultPersonalizationComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultSettingsOverviewComponent
import de.nogaemer.unspeakable.features.settings.pages.LanguageScreen
import de.nogaemer.unspeakable.features.settings.pages.PersonalizationScreen
import de.nogaemer.unspeakable.features.settings.pages.SettingsOverviewComponent
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsConfig {
    @Serializable data object Overview : SettingsConfig()
    @Serializable data object Personalization : SettingsConfig()
    @Serializable data object Language : SettingsConfig()
    @Serializable data object About : SettingsConfig()
}

interface SettingsComponent : ComponentContext {
    val stack: Value<ChildStack<SettingsConfig, MenuChild<SettingsOverviewComponent>>>
    fun resetToOverview()
    fun goBack()
}

class DefaultSettingsComponent(
    ctx: ComponentContext,
) : SettingsComponent,
    DefaultMenuComponent<SettingsConfig, SettingsOverviewComponent>(
        ctx = ctx,
        serializer = SettingsConfig.serializer(),
        initialConfig = SettingsConfig.Overview,
        childFactory = { config, childCtx, push, pop ->
            when (config) {
                is SettingsConfig.Overview -> MenuChild.Overview(
                    DefaultSettingsOverviewComponent(
                        ctx = childCtx,
                        onNavigate = push,
                    )
                )

                is SettingsConfig.Personalization -> {
                    val component = DefaultPersonalizationComponent(childCtx, pop)
                    MenuChild.Page(component) {
                        PersonalizationScreen(component)
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

    override fun resetToOverview() {
        resetToRoot()
    }
}
