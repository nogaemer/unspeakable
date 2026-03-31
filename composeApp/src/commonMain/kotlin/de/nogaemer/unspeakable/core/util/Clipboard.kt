package de.nogaemer.unspeakable.core.util

import androidx.compose.ui.platform.ClipEntry

/**
 * Converts plain text into a clipboard payload for shared Compose code.
 * Android/iOS/JVM: each target bridges `ClipEntry` through platform clipboard APIs.
 */
expect fun String.toClipEntry(): ClipEntry