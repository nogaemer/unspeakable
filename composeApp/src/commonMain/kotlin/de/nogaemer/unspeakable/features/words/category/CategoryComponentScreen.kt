package de.nogaemer.unspeakable.features.words.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.util.icon.byString
import de.nogaemer.unspeakable.core.util.icon.getIconsNotInCategories
import de.nogaemer.unspeakable.db.UnspeakableCategory
import kotlin.math.roundToInt

private val EXCLUDED_ICON_CATEGORIES = listOf(
    "accessibility", "account", "arrows", "charts", "connectivity",
    "cursors", "design", "development", "files", "finance",
    "layout", "notifications", "mail", "multimedia", "photography",
    "social", "security", "time", "text", "weather",
)

private const val GRID_COLUMNS = 5

data class IconItem(val name: String, val vector: ImageVector)

@Composable
fun CategoryComponentScreen(component: CategoryComponent, modifier: Modifier = Modifier) {
    CategoryEditorContent(
        modifier = modifier,
        category = component.category,
        updateCategory = { component.category = it },
        onBack = component.onBack,
        onSave = { component.saveCategory() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategoryEditorContent(
    modifier: Modifier = Modifier,
    category: UnspeakableCategory,
    updateCategory: (UnspeakableCategory) -> Unit = {},
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    val s = strings
    var categoryName by remember(category.name) { mutableStateOf(category.name) }
    var selectedIconName by remember(category.iconName) { mutableStateOf(category.iconName) }

    val allIcons = remember {
        getIconsNotInCategories(EXCLUDED_ICON_CATEGORIES)
            .map { name -> IconItem(name = name, vector = Lucide.byString(name)) }
    }

    val colorScheme = MaterialTheme.colorScheme
    val density = LocalDensity.current

    val headerHeight = 220.dp
    val headerHeightPx = with(density) { headerHeight.toPx() }
    val gapHeight = 28.dp
    val gapHeightPx = with(density) { gapHeight.toPx() }

    val gridState = rememberLazyGridState()
    var scrollOffsetPx by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(gridState) {
        snapshotFlow {
            gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val firstItem = gridState.layoutInfo.visibleItemsInfo.firstOrNull() ?: return@collect
            if (firstItem.size.height == 0) return@collect
            val rowHeight = firstItem.size.height + gridState.layoutInfo.mainAxisItemSpacing
            scrollOffsetPx = (index / GRID_COLUMNS) * rowHeight + offset.toFloat()
        }
    }

    LaunchedEffect(categoryName, selectedIconName) {
        updateCategory(category.copy(name = categoryName, iconName = selectedIconName))
    }

    val headerOffsetY by remember {
        derivedStateOf {
            (-scrollOffsetPx).coerceIn(-headerHeightPx - gapHeightPx, 0f)
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 16.dp).padding(bottom = 16.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(GRID_COLUMNS),
                contentPadding = PaddingValues(
                    top = headerHeight + gapHeight + 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .drawBehind {
                        val bgTop = headerHeightPx + headerOffsetY + gapHeightPx
                        drawRoundRect(
                            color = colorScheme.surfaceContainer,
                            topLeft = Offset(0f, bgTop),
                            size = Size(size.width, size.height - bgTop),
                            cornerRadius = CornerRadius(32.dp.toPx()),
                        )
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(allIcons, key = { it.name }, contentType = { "icon" }) { iconItem ->
                    IconGridItem(
                        iconItem = iconItem,
                        isSelected = selectedIconName == iconItem.name,
                        onClick = { selectedIconName = iconItem.name },
                    )
                }
            }

            // Floating header — scrolls out of view in sync with the grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = headerOffsetY.roundToInt()) }
                    .padding(top = 64.dp)
                    .background(colorScheme.surfaceContainer, RoundedCornerShape(24.dp))
                    .padding(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-80).dp)
                        .clip(CircleShape)
                        .background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Lucide.byString(selectedIconName),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = colorScheme.onPrimaryContainer,
                    )
                }

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    placeholder = { Text(strings.categories.exampleName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.End),
        ) {
            FilledTonalButton(
                onClick = onBack,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                contentPadding = ButtonDefaults.MediumContentPadding,
                shapes = ButtonDefaults.shapesFor(40.dp),
            ) {
                Text(s.common.cancel)
            }

            Button(
                onClick = onSave,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                contentPadding = ButtonDefaults.MediumContentPadding,
                shapes = ButtonDefaults.shapesFor(40.dp),
            ) {
                Text(s.common.save)
            }
        }
    }
}

@Composable
private fun IconGridItem(
    iconItem: IconItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = iconItem.vector,
            contentDescription = iconItem.name,
            modifier = Modifier.size(28.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
@Preview
private fun CategoryComponentScreenPreview() {
    AppTheme(darkTheme = true) {
        CategoryEditorContent(
            modifier = Modifier.fillMaxSize(),
            category = UnspeakableCategory(id = "preview", name = "Animals", iconName = "Star"),
        )
    }
}