package de.nogaemer.unspeakable.features.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.features.home.HomeScreen
import de.nogaemer.unspeakable.features.settings.SettingsScreen
import de.nogaemer.unspeakable.features.words.CategoriesScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(component: MainComponent) {
    val pages by component.pages.subscribeAsState()
    val active = pages.items[pages.selectedIndex].instance
    val selectedTab by component.selectedTab.subscribeAsState()
    val text = strings.nav

    val showBottomBar = when (active) {
        is MainComponent.TabChild.Settings -> {
            val child = active.component.stack.subscribeAsState().value.active.instance
            child is MenuChild.Overview
        }
        is MainComponent.TabChild.Category -> {
            val child = active.component.stack.subscribeAsState().value.active.instance
            child is MenuChild.Overview
        }
        else -> true
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {

                ShortNavigationBar(
//                    if (active is MainComponent.TabChild.Settings) {
//                        Modifier.dropShadow(
//                            shape = RoundedCornerShape(0.dp),
//                            shadow = Shadow(
//                                radius = 8.dp,
//                                spread = 4.dp,
//                                color = if (LocalAppSettings.current.appSettings.isDark) Color(
//                                    0x40000000
//                                ) else Color(
//                                    0x40636363
//                                ),
//                                offset = DpOffset(x = 4.dp, 4.dp)
//                            )
//                        )
//                    } else {
//                        Modifier
//                    }
                ) {
                    Tab.entries.forEach { tab ->
                        ShortNavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { component.onTabSelected(tab) },
                            icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        Tab.HOME -> Lucide.House
                                        Tab.WORDS -> Lucide.BookOpen
                                        Tab.SETTINGS -> Lucide.Settings
                                    },
                                    contentDescription = when (tab) {
                                        Tab.HOME -> text.home
                                        Tab.WORDS -> text.words
                                        Tab.SETTINGS -> text.settings
                                    }
                                )
                            },
                            label = {
                                Text(
                                    when (tab) {
                                        Tab.HOME -> text.home
                                        Tab.WORDS -> text.words
                                        Tab.SETTINGS -> text.settings
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        val animatedBottomPadding by animateDpAsState(
            targetValue = if (showBottomBar) padding.calculateBottomPadding() else 0.dp,
            spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = 0.5.dp,
            ),
            label = "main_bottom_padding"
        )

        Box(
            Modifier.padding(
                start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                top = 0.dp,
                end = padding.calculateRightPadding(LayoutDirection.Ltr),
                bottom = animatedBottomPadding,
            )
        ) {
            when (active) {
                is MainComponent.TabChild.Home -> HomeScreen(active.component)
                is MainComponent.TabChild.Category -> CategoriesScreen(active.component)
                is MainComponent.TabChild.Settings -> SettingsScreen(active.component)
                null -> Unit
            }
        }
    }
}
