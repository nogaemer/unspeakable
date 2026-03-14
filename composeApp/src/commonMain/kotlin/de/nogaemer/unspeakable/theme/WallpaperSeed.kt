package de.nogaemer.unspeakable.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@Composable
expect fun rememberWallpaperSeedColor(isDarkMode: Boolean): Color?