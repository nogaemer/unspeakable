package de.nogaemer.unspeakable.features.settings.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.menu.OverviewItem
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.features.settings.SettingsConfig
import de.nogaemer.unspeakable.features.settings.SettingsOverviewComponent

@Composable
fun SettingsOverviewScreen(
    component: SettingsOverviewComponent<SettingsConfig>
) {
    SettingsOverviewContent(
        items = component.items,
        onItemClick = { component.navigateTo(it) }
    )
}

@Composable
fun SettingsOverviewContent(
    items: List<List<OverviewItem<SettingsConfig>>>,
    onItemClick: (SettingsConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = strings
    Surface(modifier = modifier) {
        LazyColumn {
            itemsIndexed(items) { _, group ->
                SegmentedColumn {
                    group.forEach { item ->
                        SegmentedListItem(
                            leadingContent = { Icon(item.icon, contentDescription = null) },
                            headlineContent = { Text(item.titleKey(s)) },
                            supportingContent = item.descriptionKey?.let { description ->
                                { Text(description(s)) }
                            },
                            modifier = Modifier.clickable { onItemClick(item.config) },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingsOverviewScreenPreview() {
    val lyricist = rememberStrings()
    ProvideStrings(lyricist) {
        AppTheme {
            SettingsOverviewContent(
                items = listOf(
                    listOf(
                        OverviewItem(
                            config = SettingsConfig.Personalization,
                            icon = Lucide.Palette,
                            titleKey = { "Personalization" },
                            descriptionKey = { "Colors, Theme, etc." },
                        ),
                        OverviewItem(
                            config = SettingsConfig.Language,
                            icon = Lucide.Globe,
                            titleKey = { "Language" },
                            descriptionKey = { "English" },
                        ),
                    ),
                    listOf(
                        OverviewItem(
                            config = SettingsConfig.About,
                            icon = Lucide.Info,
                            titleKey = { "About" },
                            descriptionKey = null,
                        ),
                    ),
                ),
                onItemClick = {}
            )
        }
    }
}
