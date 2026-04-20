package de.nogaemer.unspeakable.core.util.file

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberJsonFilePicker(
    onJsonPicked: (String) -> Unit,
    onPickFailed: (Throwable?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        runCatching {
            context.contentResolver.openInputStream(uri)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: error("Unable to open selected file")
        }
            .onSuccess(onJsonPicked)
            .onFailure(onPickFailed)
    }

    return {
        launcher.launch(arrayOf("application/json", "text/plain"))
    }
}

