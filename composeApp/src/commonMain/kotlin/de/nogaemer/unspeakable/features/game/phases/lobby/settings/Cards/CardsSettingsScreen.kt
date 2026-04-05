package de.nogaemer.unspeakable.features.game.phases.lobby.settings.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.i18n.categoryName
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.db.Graph

@Composable
fun CardsSettingsScreen(component: DefaultCardsSettingsComponent) {
    val state by component.state.collectAsState()
    val settings = state.match?.settings ?: return
    val s = strings

    val categoryIdsFromDb by produceState(initialValue = emptyList<String>()) {
        value = runCatching {
            Graph.categoriesDao.getAllCategories().map { it.id }
        }.getOrDefault(emptyList())
    }

    val allCategoryIds = (categoryIdsFromDb.ifEmpty { s.game.categoryStrings.keys.toList() })
        .distinct()
        .sortedBy { s.categoryName(it) }

    fun isSelected(categoryId: String): Boolean {
        val selected = settings.selectedCategoryIds
        return selected.isEmpty() || categoryId in selected
    }

    fun persistSelection(next: Set<String>) {
        if (next.isEmpty()) return

        val normalized = if (next.size == allCategoryIds.size) {
            emptySet() // "all"
        } else {
            next
        }

        component.onEvent(
            GameClientEvent.UpdateGameSettings(
                settings.copy(selectedCategoryIds = normalized)
            )
        )
    }

    SegmentedLazyColumn(
        segmentTitle = s.gameLobbySettings.categoriesSettingsStrings.categoriesTitle,
    ) {
        items(allCategoryIds, key = { it }) { categoryId ->
            val selected = isSelected(categoryId)
            SegmentedListItem(
                selected = selected,
                modifier = Modifier.clickable {
                    val currentSelection = settings.selectedCategoryIds.ifEmpty {
                        allCategoryIds.toSet()
                    }

                    val nextSelection = if (selected) {
                        currentSelection - categoryId
                    } else {
                        currentSelection + categoryId
                    }

                    persistSelection(nextSelection)
                },
                headlineContent = {
                    Text(s.categoryName(categoryId))
                },
                trailingContent = {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { isChecked ->
                            val currentSelection = settings.selectedCategoryIds.ifEmpty {
                                allCategoryIds.toSet()
                            }

                            val nextSelection = if (isChecked) {
                                currentSelection + categoryId
                            } else {
                                currentSelection - categoryId
                            }

                            persistSelection(nextSelection)
                        }
                    )
                }
            )
        }
    }
}
