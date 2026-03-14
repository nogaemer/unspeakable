package de.nogaemer.unspeakable.features.settings

import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.features.settings.DefaultSettingsComponent.SettingsChild.About
import de.nogaemer.unspeakable.features.settings.DefaultSettingsComponent.SettingsChild.Language
import de.nogaemer.unspeakable.features.settings.DefaultSettingsComponent.SettingsChild.Overview
import de.nogaemer.unspeakable.features.settings.pages.AboutComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultAboutComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultLanguageComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultPersonalizationComponent
import de.nogaemer.unspeakable.features.settings.pages.DefaultSettingsOverviewComponent
import de.nogaemer.unspeakable.features.settings.pages.LanguageComponent
import de.nogaemer.unspeakable.features.settings.pages.PersonalizationComponent
import de.nogaemer.unspeakable.features.settings.pages.SettingsOverviewComponent
import kotlinx.serialization.Serializable

interface SettingsPage {
    val titleKey: (Strings) -> String
    val descriptionKey: ((Strings) -> String)? get() = null
    val icon: ImageVector
}

@Serializable
sealed class SettingsConfig {
    @Serializable data object Overview : SettingsConfig()
    @Serializable data object Personalization : SettingsConfig()
    @Serializable data object Language : SettingsConfig()
    @Serializable data object About : SettingsConfig()
}

interface SettingsComponent : ComponentContext {
    val stack: Value<ChildStack<SettingsConfig, DefaultSettingsComponent.SettingsChild>>
    fun resetToOverview()
    fun goBack()
}

class DefaultSettingsComponent(
    ctx: ComponentContext,
) : SettingsComponent, ComponentContext by ctx {

    private val navigation = StackNavigation<SettingsConfig>()

    override val stack = childStack(
        source = navigation,
        serializer = SettingsConfig.serializer(),
        initialConfiguration = SettingsConfig.Overview,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(config: SettingsConfig, ctx: ComponentContext): SettingsChild {
        val personalization = DefaultPersonalizationComponent(ctx, _onBack = { navigation.pop() })
        val language = DefaultLanguageComponent(ctx, _onBack = { navigation.pop() })
        val about = DefaultAboutComponent(ctx, _onBack = { navigation.pop() })

        return when (config) {
            is SettingsConfig.Overview -> Overview(
                DefaultSettingsOverviewComponent(
                    ctx = ctx,
                    personalization = personalization,
                    language = language,
                    about = about,
                    _onNavigate = { navigation.push(it) }
                )
            )

            is SettingsConfig.Personalization -> SettingsChild.Personalization(personalization)
            is SettingsConfig.Language -> Language(language)
            is SettingsConfig.About -> About(about)
        }
    }

    override fun goBack() {
        navigation.pop()
    }

    override fun resetToOverview() {
        navigation.replaceAll(SettingsConfig.Overview)
    }

    sealed class SettingsChild {
        data class Overview(val component: SettingsOverviewComponent) : SettingsChild()
        data class Personalization(val component: PersonalizationComponent) : SettingsChild()
        data class Language(val component: LanguageComponent) : SettingsChild()
        data class About(val component: AboutComponent) : SettingsChild()
    }
}
