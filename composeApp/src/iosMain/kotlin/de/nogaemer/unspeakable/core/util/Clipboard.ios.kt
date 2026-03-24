package de.nogaemer.unspeakable.core.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

// Source - https://stackoverflow.com/a/68389985
// Posted by Phil Dukhov, modified by community. See post 'Timeline' for change history
// Retrieved 2026-03-21, License - CC BY-SA 4.0

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry() =
    ClipEntry.withPlainText(this)
