package de.nogaemer.unspeakable.features.settings.pages.personalization.palette_style

import androidx.compose.foundation.clickable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem

@Composable
fun PaletteStyleScreen(
    component: DefaultPaletteStyleComponent
) {

    SegmentedLazyColumn {
        (component.paletteStyles).forEach { style ->
            item {
                SegmentedListItem(
                    modifier = Modifier.clickable { component.selectPaletteStyle(style) },
                    selected = component.isSelected(style),
                    headlineContent = { Text(style.name) },
                    trailingContent = {
                        RadioButton(
                            selected = component.isSelected(style),
                            onClick = {
                                component.selectPaletteStyle(style)
                            }
                        )
                    }
                )
            }
        }
    }
}