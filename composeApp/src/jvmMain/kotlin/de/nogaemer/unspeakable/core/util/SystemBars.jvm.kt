package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable

/**
 * Keeps shared theming API consistent on desktop targets.
 * JVM: no-op because this layer does not control OS status-bar styling.
 */
@Composable
actual fun SystemBarAppearance(darkTheme: Boolean) {
}