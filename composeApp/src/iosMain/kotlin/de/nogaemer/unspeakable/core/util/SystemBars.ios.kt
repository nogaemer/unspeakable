package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable

/**
 * Keeps shared system-bar API available for iOS UI code.
 * iOS: currently a no-op until UIKit status-bar styling integration is added.
 */
@Composable
actual fun SystemBarAppearance(darkTheme: Boolean) {
}