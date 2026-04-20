package de.nogaemer.unspeakable.features.words.words

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem

@Composable
fun WordsOverviewScreen(
    component: WordsComponent,
) {
    val words by component.words.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { component.navigate(WordsConfig.NewWord(component.category)) }
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = null
                )
            }
        }
    ) {
        SegmentedLazyColumn {
            items(words) {
                SegmentedListItem(
                    modifier = Modifier.clickable {
                        component.navigate(WordsConfig.EditWord(it))
                    },
                    headlineContent = { Text(it.word) },
                )
            }
        }
    }
}