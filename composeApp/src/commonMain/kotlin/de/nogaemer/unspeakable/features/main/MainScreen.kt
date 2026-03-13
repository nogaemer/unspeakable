package de.nogaemer.unspeakable.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import de.nogaemer.unspeakable.features.home.HomeScreen
import de.nogaemer.unspeakable.features.settings.SettingsScreen
import de.nogaemer.unspeakable.features.words.WordsScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(component: MainComponent) {
    val pages by component.pages.subscribeAsState()
    val selectedTab by component.selectedTab.subscribeAsState()

    Scaffold(
        bottomBar = {
            ShortNavigationBar {
                Tab.entries.forEach { tab ->
                    ShortNavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { component.onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    Tab.HOME     -> Lucide.House
                                    Tab.WORDS    -> Lucide.BookOpen
                                    Tab.SETTINGS -> Lucide.Settings
                                },
                                contentDescription = tab.name
                            )
                        },
                        label = {
                            Text(
                                when (tab) {
                                    Tab.HOME     -> "Home"
                                    Tab.WORDS    -> "Words"
                                    Tab.SETTINGS -> "Settings"
                                }
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (val active = pages.items[pages.selectedIndex].instance) {
                is MainComponent.TabChild.Home     -> HomeScreen(active.component)
                is MainComponent.TabChild.Words    -> WordsScreen(active.component)
                is MainComponent.TabChild.Settings -> SettingsScreen(active.component)
                null -> Unit
            }
        }
    }
}

