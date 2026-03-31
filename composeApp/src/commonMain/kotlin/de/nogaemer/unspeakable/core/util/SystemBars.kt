package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable

/**
 * Applies system bar appearance to keep top-bar content legible in theme changes.
 * Android/iOS/JVM: behavior varies by target capabilities in actuals.
 */
@Composable
expect fun SystemBarAppearance(darkTheme: Boolean)