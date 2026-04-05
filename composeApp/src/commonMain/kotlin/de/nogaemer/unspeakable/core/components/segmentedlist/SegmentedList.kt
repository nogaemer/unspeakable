package de.nogaemer.unspeakable.core.components.segmentedlist


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders a lazy segmented container with optional section title styling.
 */
@Composable
fun SegmentedLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(12.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.spacedBy(2.dp, Alignment.Top)
        else Arrangement.spacedBy(2.dp, Alignment.Bottom),

    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    segmentTitle: String? = null,
    content: LazyListScope.() -> Unit,
) {
    Column(
        Modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {
        if (segmentTitle != null) Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = segmentTitle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        LazyColumn(
            modifier = modifier.clip(RoundedCornerShape(20.dp)),
            state = state,
            flingBehavior = flingBehavior,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            reverseLayout = reverseLayout,
            userScrollEnabled = userScrollEnabled,
            overscrollEffect = overscrollEffect,
            content = content,
        )
    }
}

/**
 * Renders a non-lazy segmented container with consistent section styling.
 */
@Composable
fun SegmentedColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.spacedBy(2.dp, Alignment.Top)
        else Arrangement.spacedBy(2.dp, Alignment.Bottom),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    segmentTitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {


    Column(
        Modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {
        if (segmentTitle != null) Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = segmentTitle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(
            modifier = modifier.clip(RoundedCornerShape(20.dp)),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}

/**
 * Renders a segmented list row with optional selected-state emphasis.
 */
@Composable
fun SegmentedListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(2.dp),
    selected: Boolean = false,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors? = null,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (selected) 20.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "SegmentedListItemCorner"
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainer,
        label = "SegmentedListItemColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.onSurface,
        label = "SegmentedListItemContentColor"
    )

    val resolvedColors = colors ?: ListItemDefaults.colors(
        containerColor = containerColor,
        headlineColor = contentColor,
    )


    Box(
        modifier = modifier.clip(
            RoundedCornerShape(cornerRadius)
        ).background(containerColor)
    ) {
        ListItem(
            headlineContent = headlineContent,
            modifier = modifier.background(resolvedColors.containerColor).padding(contentPadding),
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            colors = resolvedColors,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
        )
    }
}