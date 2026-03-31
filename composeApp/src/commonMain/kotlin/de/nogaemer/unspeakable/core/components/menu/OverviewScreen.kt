package de.nogaemer.unspeakable.core.components.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem

/**
 * Renders grouped overview items that navigate to menu destinations.
 */
@Composable
fun <Config : Any> OverviewScreen(component: SimpleOverviewComponent<Config>) {
    val s = strings

    Surface {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            component.items.forEach { group ->
                item {
                    SegmentedColumn(contentPadding = PaddingValues(0.dp)) {
                        group.forEach { item ->
                            SegmentedListItem(
                                modifier = Modifier.clickable { component.navigateTo(item.config) },
                                leadingContent = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                    )
                                },
                                headlineContent = { Text(item.titleKey(s)) },
                                supportingContent = item.descriptionKey?.let { desc ->
                                    { Text(desc(s)) }
                                },
                                trailingContent = {
                                    Icon(
                                        imageVector = Lucide.ChevronRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}