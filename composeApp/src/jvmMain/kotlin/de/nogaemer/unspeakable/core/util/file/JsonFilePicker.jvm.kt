package de.nogaemer.unspeakable.core.util.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.nio.charset.StandardCharsets

@Composable
actual fun rememberJsonFilePicker(
    onJsonPicked: (String) -> Unit,
    onPickFailed: (Throwable?) -> Unit,
): () -> Unit {
    return remember(onJsonPicked, onPickFailed) {
        {
            if (isWindows()) {
                pickJsonFileOnWindows(onJsonPicked, onPickFailed)
            } else {
                pickJsonFileOnDesktopFallback(onJsonPicked, onPickFailed)
            }
        }
    }
}

private fun pickJsonFileOnWindows(
    onJsonPicked: (String) -> Unit,
    onPickFailed: (Throwable?) -> Unit,
) {
    runCatching {
        val script = """
            Add-Type -AssemblyName System.Windows.Forms
            ${'$'}dialog = New-Object System.Windows.Forms.OpenFileDialog
            ${'$'}dialog.Filter = 'JSON files (*.json)|*.json|All files (*.*)|*.*'
            ${'$'}dialog.Multiselect = ${'$'}false
            ${'$'}dialog.CheckFileExists = ${'$'}true
            ${'$'}dialog.Title = 'Select JSON file'
            if (${'$'}dialog.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) {
                [Console]::Out.WriteLine(${'$'}dialog.FileName)
            }
        """.trimIndent()

        val process = ProcessBuilder(
            "powershell.exe",
            "-NoProfile",
            "-STA",
            "-Command",
            script,
        )
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            error(output.trim().ifBlank { "Windows file picker failed" })
        }

        output.lineSequence().firstOrNull()?.trim().orEmpty()
    }.onSuccess { path ->
        if (path.isBlank()) return@onSuccess
        runCatching {
            File(path).readText(StandardCharsets.UTF_8)
        }.onSuccess(onJsonPicked)
            .onFailure(onPickFailed)
    }.onFailure(onPickFailed)
}

private fun pickJsonFileOnDesktopFallback(
    onJsonPicked: (String) -> Unit,
    onPickFailed: (Throwable?) -> Unit,
) {
    runCatching {
        val dialog = FileDialog(null as Frame?, "Select JSON file", FileDialog.LOAD).apply {
            filenameFilter = FilenameFilter { _, name ->
                name.endsWith(".json", ignoreCase = true)
            }
            isVisible = true
        }

        val fileName = dialog.file ?: return@runCatching null
        val directory = dialog.directory ?: return@runCatching null
        File(directory, fileName)
    }.onSuccess { selectedFile ->
        if (selectedFile == null) return@onSuccess
        runCatching {
            selectedFile.readText(StandardCharsets.UTF_8)
        }.onSuccess(onJsonPicked)
            .onFailure(onPickFailed)
    }.onFailure(onPickFailed)
}

private fun isWindows(): Boolean =
    System.getProperty("os.name")?.contains("windows", ignoreCase = true) == true
