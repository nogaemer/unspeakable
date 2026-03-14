package de.nogaemer.unspeakable.theme

import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberWallpaperSeedColor(isDarkMode: Boolean): Color? {
    val context = LocalContext.current
    return remember {
        val colorScheme =
            if (isDarkMode) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        colorScheme.primary
    }
}