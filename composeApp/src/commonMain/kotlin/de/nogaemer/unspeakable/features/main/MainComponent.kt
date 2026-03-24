package de.nogaemer.unspeakable.features.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import de.nogaemer.unspeakable.features.game_setup.NetworkMode
import de.nogaemer.unspeakable.features.home.DefaultHomeComponent
import de.nogaemer.unspeakable.features.home.HomeComponent
import de.nogaemer.unspeakable.features.settings.DefaultSettingsComponent
import de.nogaemer.unspeakable.features.settings.SettingsComponent
import de.nogaemer.unspeakable.features.words.DefaultWordsComponent
import de.nogaemer.unspeakable.features.words.WordsComponent
import kotlinx.serialization.Serializable

@Serializable
enum class Tab { HOME, WORDS, SETTINGS }


interface MainComponent {
    val pages: Value<ChildPages<*, TabChild>>
    val selectedTab: Value<Tab>
    fun onTabSelected(tab: Tab)

    sealed class TabChild {
        data class Home(val component: HomeComponent)         : TabChild()
        data class Words(val component: WordsComponent)       : TabChild()
        data class Settings(val component: SettingsComponent) : TabChild()
    }
}

class DefaultMainComponent(
    ctx: ComponentContext,
    private val onSelect: (NetworkMode) -> Unit
) : MainComponent, ComponentContext by ctx {

    private val navigation = PagesNavigation<Tab>()

    override val pages = childPages(
        source = navigation,
        serializer = Tab.serializer(),
        initialPages = { Pages(items = Tab.entries, selectedIndex = 0) },
        childFactory = ::createTab
    )

    override val selectedTab: Value<Tab> = pages.map { childPages ->
        Tab.entries[childPages.selectedIndex]
    }


    override fun onTabSelected(tab: Tab) {
        navigation.select(index = Tab.entries.indexOf(tab))

        // resets when navigating to different tab on navbar
        if (tab == Tab.SETTINGS) {
            val settingsChild = pages.value.items
                .firstOrNull { it.instance is MainComponent.TabChild.Settings }
                ?.instance as? MainComponent.TabChild.Settings
            settingsChild?.component?.resetToOverview()
        }

    }

    private fun createTab(tab: Tab, ctx: ComponentContext): MainComponent.TabChild =
        when (tab) {
            Tab.HOME     -> MainComponent.TabChild.Home(
                DefaultHomeComponent(ctx, onSelect = onSelect)
            )
            Tab.WORDS    -> MainComponent.TabChild.Words(DefaultWordsComponent(ctx))
            Tab.SETTINGS -> MainComponent.TabChild.Settings(DefaultSettingsComponent(ctx))
        }
}
