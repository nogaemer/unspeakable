package de.nogaemer.unspeakable.features.words

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import co.touchlab.kermit.Logger
import com.composables.icons.lucide.Layers2
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.ScrollText
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.util.icon.byString
import de.nogaemer.unspeakable.db.UnspeakableCategory

@Composable
fun CategoryOverviewScreen(
    component: CategoryComponent
) {
    val categories by component.categories.collectAsState()
    Logger.d("Categories: $categories")

    CategoryOverviewScreen(
        categories = categories,
        onCategoryClick = { category ->
            component.onCategoryClick(category)
        },
        navigate = component::navigate
    )
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CategoryOverviewScreen(
    categories: List<UnspeakableCategory>,
    onCategoryClick: (UnspeakableCategory) -> Unit,
    navigate: (CategoryConfig) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }


    Scaffold(
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = expanded,
                button = {
                    ToggleFloatingActionButton(
                        checked = expanded,
                        onCheckedChange = { expanded = !expanded },
                    ) {
                        val tint = animateColorAsState(
                            if (expanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                            label = "fab_icon_tint"
                        )

                        Icon(
                            tint = tint.value,
                            imageVector = Lucide.Plus,
                            contentDescription = null,
                            modifier = Modifier.rotate(checkedProgress * 45f).size(24.dp)
                        )
                    }
                }
            ) {

                FloatingActionButtonMenuItem(
                    onClick = { expanded = false; navigate(CategoryConfig.NewWord) },
                    icon = { Icon(Lucide.ScrollText, contentDescription = null) },
                    text = { Text(strings.common.addWord) }
                )
                FloatingActionButtonMenuItem(
                    onClick = { expanded = false; navigate(CategoryConfig.NewCategory) },
                    icon = { Icon(Lucide.Layers2, contentDescription = null) },
                    text = { Text(strings.common.addCategory) }
                )
            }
        }
    ) {
        SegmentedLazyColumn {
            items(categories) {
                SegmentedListItem(
                    modifier = Modifier.clickable { onCategoryClick(it) },
                    leadingContent = {
                        Icon(
                            imageVector = Lucide.byString(it.iconName),
                            contentDescription = null,
                        )
                    },
                    headlineContent = { Text(it.getTranslatedName(strings)) },
                )
            }
        }
    }
}