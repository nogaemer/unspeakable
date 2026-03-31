package de.nogaemer.unspeakable.core.components.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.materialkolor.hct.Hct
import de.nogaemer.unspeakable.core.util.settings.LocalAppSettings
import de.nogaemer.unspeakable.core.util.settings.isDark
import kotlin.math.roundToInt

/**
 * Provides a draggable hue slider for theme personalization.
 */
@Composable
fun RainbowSlider(
    hue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    val strokeWidth by animateFloatAsState(targetValue = if (isDragged) 6f else 2f)

    BoxWithConstraints(
        modifier = modifier
            .height(32.dp)
            .fillMaxWidth()
    ) {
        val width = constraints.maxWidth.toFloat()
        val density = LocalDensity.current
        val thumbRadius = with(density) { 16.dp.toPx() } // Corrected: thumbRadius is px
        val trackHeight = with(density) { 32.dp.toPx() }

        val sliderWidth = width - (thumbRadius * 2)
        val offset = (hue / 360f) * sliderWidth

        val isDark = LocalAppSettings.current.appSettings.isDark

        fun seedToPrimary(seed: Color, isDark: Boolean): Color {
            val hct = Hct.fromInt(seed.toArgb())
            val tone = if (isDark) 80.0 else 60.0
            return Color(Hct.from(hct.hue, maxOf(hct.chroma, 48.0), tone).toInt())
        }

        val gradientColors = remember(isDark) {
            (0..360 step 4).map { h ->
                seedToPrimary(Color.hsv(h.toFloat(), 0.6f, 1f), isDark)
            }
        }



        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(colors = gradientColors))
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offset.roundToInt(), 0) }
                .size(32.dp)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newOffset = (offset + delta).coerceIn(0f, sliderWidth)
                        val newHue = (newOffset / sliderWidth) * 360f
                        onHueChanged(newHue)
                    },
                    interactionSource = interactionSource
                ),
            contentAlignment = Alignment.Center
        ) {

            val innerColor = MaterialTheme.colorScheme.primary

            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2
                drawCircle(
                    color = innerColor,
                    radius = r - (strokeWidth * density.density) // strokeWidth logic
                )
            }
        }
    }
}

