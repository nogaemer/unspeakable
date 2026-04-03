package de.nogaemer.unspeakable.features.game.phases.playing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

private enum class TwoPartSlot { TopIntrinsic, Bottom, TopFinal }

@Composable
fun TwoPartLayout(
    modifier: Modifier = Modifier,
    top: @Composable () -> Unit,
    bottom: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val width = constraints.maxWidth
        val totalHeight = constraints.maxHeight

        val topNaturalHeight = subcompose(TwoPartSlot.TopIntrinsic, top)
            .sumOf {
                it.measure(
                    Constraints(
                        minWidth = width,
                        maxWidth = width,
                        minHeight = 0,
                        maxHeight = Constraints.Infinity
                    )
                ).height
            }

        val bottomMaxHeight = (totalHeight - topNaturalHeight).coerceAtLeast(0)
        val bottomPlaceables = subcompose(TwoPartSlot.Bottom, bottom)
            .map {
                it.measure(
                    Constraints(
                        minWidth = width,
                        maxWidth = width,
                        minHeight = 0,
                        maxHeight = bottomMaxHeight
                    )
                )
            }
        val bottomActualHeight = bottomPlaceables.sumOf { it.height }

        val topFinalHeight = totalHeight - bottomActualHeight
        val topPlaceables = subcompose(TwoPartSlot.TopFinal, top)
            .map {
                it.measure(
                    Constraints(
                        minWidth = width,
                        maxWidth = width,
                        minHeight = topFinalHeight,
                        maxHeight = topFinalHeight
                    )
                )
            }

        layout(width, totalHeight) {
            topPlaceables.forEach { it.placeRelative(0, 0) }
            bottomPlaceables.forEach { it.placeRelative(0, topFinalHeight) }
        }
    }
}
