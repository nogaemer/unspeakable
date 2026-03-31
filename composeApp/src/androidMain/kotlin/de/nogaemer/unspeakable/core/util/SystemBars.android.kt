package de.nogaemer.unspeakable.core.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Updates Android status-bar icon contrast to match app theme.
 * Android: uses `WindowCompat` insets controller to toggle light status-bar icons.
 */
@Composable
actual fun SystemBarAppearance(darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }
}
