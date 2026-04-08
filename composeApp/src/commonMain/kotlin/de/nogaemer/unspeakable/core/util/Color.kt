package de.nogaemer.unspeakable.core.util

import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.model.RGB

fun Color.withHue(newHue: Float): Color {
    val oklch = RGB(red, green, blue).toOklch()

    val rotated = oklch.copy(h = newHue.coerceIn(0f, 360f))
    val result = rotated.toSRGB().clamp()
    return Color(
        red = result.r,
        green = result.g,
        blue = result.b,
        alpha = alpha
    )
}