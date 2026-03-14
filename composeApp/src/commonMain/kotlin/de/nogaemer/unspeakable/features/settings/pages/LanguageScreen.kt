package de.nogaemer.unspeakable.features.settings.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.util.settings.LocalAppSettings
import de.nogaemer.unspeakable.core.util.settings.Locales

@Composable
fun LanguageScreen(component: LanguageComponent) {
    val appSettings = LocalAppSettings.current
    val currentTag = appSettings.appSettings.locales
    val s = strings.settings.languageStrings.languages


    Surface {
        SegmentedLazyColumn {
            items(Locales.entries, key = { it.lang + "asdf" }) { locale ->
                val text = s[locale]!!
                val selected = false

                SegmentedListItem(
                    selected = selected,
                    headlineContent = { Text("Test") },
                    supportingContent = { Text("Test description") },
                    trailingContent = {
                        if (selected) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null
                            )
                        }

                    },
                    modifier = Modifier.clickable {
                        appSettings.setLanguage(locale)
                        component.onLanguageSelected(locale)
                    }

                )
            }
            items(Locales.entries, key = { it.lang }) { locale ->
                val text = s[locale]!!
                val selected = locale == currentTag

                SegmentedListItem(
                    selected = selected,
                    headlineContent = { Text(text.name) },
                    supportingContent = { Text(text.translatedName) },
                    trailingContent = {
                        if (selected) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null
                            )
                        }

                    },
                    modifier = Modifier.clickable {
                        appSettings.setLanguage(locale)
                        component.onLanguageSelected(locale)
                    }

                )
            }
        }
    }
}
