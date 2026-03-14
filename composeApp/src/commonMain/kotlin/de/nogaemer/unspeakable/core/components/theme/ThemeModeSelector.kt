package de.nogaemer.unspeakable.core.components.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.nogaemer.unspeakable.core.util.settings.ThemeMode
import kotlin.math.pow

private val BackOutAlignmentEasing = Easing { t ->
    val s = 1.2f
    val p = t - 1f
    1f + p * p * ((s + 1f) * p + s)
}

private fun shapeFor(t: Float): Pair<Float, Float> {
    val tDouble = t.toDouble()
    val w = 2.67 * tDouble.pow(4) - 3.2 * tDouble.pow(3) - 0.24 * tDouble.pow(2) + 0.77 * tDouble + 1
    val h = -2.95 * tDouble.pow(4) + 3.33 * tDouble.pow(3) + 0.44 * tDouble.pow(2) - 0.82 * tDouble + 1
    return w.toFloat() to h.toFloat()
}

@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    val personalizationStrings = LocalStrings.current.settings.personalizationStrings

    val items = listOf(
        ThemeMode.SYSTEM to personalizationStrings.systemModeLabel,
        ThemeMode.LIGHT to personalizationStrings.lightModeLabel,
        ThemeMode.DARK to personalizationStrings.darkModeLabel
    )

    val selectedIndex = items.indexOfFirst { it.first == selected }

    val shapeAnim = remember { Animatable(1f) }

    LaunchedEffect(selected) {
        shapeAnim.snapTo(0f)
        shapeAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = CubicBezierEasing(0.215f, 0.61f, 0.355f, 1.0f) // easeOutCubic
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .height(56.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        val maxWidth = maxWidth
        val itemWidth = maxWidth / 3

        // Calculate bias: -1 (left), 0 (center), 1 (right)
        val biasTarget = when(selectedIndex) {
            0 -> -1f
            1 -> 0f
            else -> 1f
        }

        val bias by animateFloatAsState(
            targetValue = biasTarget,
            animationSpec = tween(
                durationMillis = 250,
                easing = BackOutAlignmentEasing
            )
        )

        val (wMul, hMul) = shapeFor(shapeAnim.value)

        // Bouncy Pill
        Box(
            modifier = Modifier
                .height(maxHeight * hMul)
                .width(itemWidth * wMul)
                .align(BiasAlignment(bias, 0f))
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )

        // Labels
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEachIndexed { index, (mode, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelect(mode) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val isSelected = index == selectedIndex

                    val textColor = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Text(
                        text = label,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
