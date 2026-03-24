package de.nogaemer.unspeakable.core.util

import androidx.compose.ui.platform.toClipEntry

// Source - https://stackoverflow.com/a/68389985
// Posted by Phil Dukhov, modified by community. See post 'Timeline' for change history
// Retrieved 2026-03-21, License - CC BY-SA 4.0

actual fun String.toClipEntry() =
    android.content.ClipData.newPlainText(this, this).toClipEntry()
