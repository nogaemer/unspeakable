package de.nogaemer.unspeakable.features.settings.pages.personalization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.components.theme.RainbowSlider
import de.nogaemer.unspeakable.core.components.theme.ThemeModeSelector
import de.nogaemer.unspeakable.core.util.settings.LocalAppSettings
import de.nogaemer.unspeakable.core.util.settings.isDark
import de.nogaemer.unspeakable.features.settings.pages.personalization.palette_style.DefaultPaletteStyleComponent


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PersonalizationScreen(
    component: DefaultPersonalizationComponent
) {
    val controller = LocalAppSettings.current
    val strings = LocalStrings.current
    val settings = controller.appSettings
    val isDark = settings.isDark
    val personalizationStrings = strings.settings.personalizationStrings


    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Theme Mode Selector
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ThemeModeSelector(
                selected = settings.themeMode,
                onSelect = { controller.setThemeMode(it) }
            )
        }


        // Dyn. color + amoled
        SegmentedColumn(
            segmentTitle = personalizationStrings.themeSectionTitle
        ) {
            SegmentedListItem(
                headlineContent = {
                    Text(personalizationStrings.amoledLabel)
                },
                modifier = Modifier.clickable(
                    onClick = {
                        component.toggleDarkMode(isDark)
                    }
                ),
                trailingContent = {
                    Switch(
                        checked = settings.isAmoled,
                        onCheckedChange = {
                            component.toggleDarkMode(isDark, it)
                        },
                        enabled = settings.isDark
                    )
                }
            )

            Column(
                Modifier.clip(
                    RoundedCornerShape(4.dp)
                ).background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )

            ) {
                SegmentedListItem(
                    modifier = Modifier.clickable(
                        onClick = {
                            component.toggleUseDynamicColor()
                        }
                    ),
                    headlineContent = {
                        Text(personalizationStrings.dynamicColorLabel)
                    },
                    supportingContent = {
                        Text(personalizationStrings.dynamicColorDescription)
                    },
                    trailingContent = {
                        Switch(
                            checked = settings.useDynamicColor,
                            onCheckedChange = {
                                component.toggleUseDynamicColor(it)
                            }
                        )
                    },
                )

                val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()

                AnimatedVisibility(
                    visible = !settings.useDynamicColor,
                    enter = expandVertically(spatialSpec),
                    exit = shrinkVertically(spatialSpec),
                    label = "theme_mode_selector"
                ) {

                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                    ) {
                        RainbowSlider(
                            hue = settings.hue,
                            onHueChanged = { controller.setHue(it) }
                        )
                    }
                }
            }

            SegmentedListItem(
                leadingContent = {
                    Icon(
                        DefaultPaletteStyleComponent.icon,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text(DefaultPaletteStyleComponent.titleKey(strings))
                },
                supportingContent = { Text(DefaultPaletteStyleComponent.descriptionKey(strings)) },
                modifier = Modifier.clickable(
                    onClick = {
                        component.onNavigate(PersonalizationSettingsConfig.PaletteStyle)
                    }
                )
            )
        }
    }
}