package de.nogaemer.unspeakable.features.settings.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem

@Composable
fun SettingsOverviewScreen(
    component: SettingsOverviewComponent
) {
    val s = strings
    LazyColumn {
        items(component.pages) { group ->
            SegmentedColumn {
                for (entry in group) {
                    val page = entry.page
                    SegmentedListItem(
                        leadingContent = { Icon(page.icon, contentDescription = null) },
                        headlineContent = { Text(page.titleKey(s)) },
                        supportingContent = {
                            if (page.descriptionKey != null) Text(page.descriptionKey!!(s))
                        },
                        modifier = Modifier.clickable { component.onNavigate(entry.config) }
                    )
                }

            }
        }
    }
}