package de.nogaemer.unspeakable.core.util.file

import androidx.compose.runtime.Composable

/**
 * Returns a launcher callback that opens a JSON file picker and reads file text.
 */
@Composable
expect fun rememberJsonFilePicker(
    onJsonPicked: (String) -> Unit,
    onPickFailed: (Throwable?) -> Unit,
): () -> Unit

