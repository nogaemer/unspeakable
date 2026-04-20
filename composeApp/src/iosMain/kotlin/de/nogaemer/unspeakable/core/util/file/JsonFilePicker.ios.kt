package de.nogaemer.unspeakable.core.util.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun rememberJsonFilePicker(
    onJsonPicked: (String) -> Unit,
    onPickFailed: (Throwable?) -> Unit,
): () -> Unit {
    val pickedState = rememberUpdatedState(onJsonPicked)
    val failedState = rememberUpdatedState(onPickFailed)
    val delegateHolder = remember { mutableStateOf<JsonDocumentPickerDelegate?>(null) }

    return remember {
        {
            val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (rootController == null) {
                failedState.value(IllegalStateException("Unable to present file picker"))
            } else {
                val delegate = JsonDocumentPickerDelegate(
                    onJsonPicked = { pickedState.value(it) },
                    onPickFailed = { failedState.value(it) },
                    onFinished = { delegateHolder.value = null },
                )
                delegateHolder.value = delegate

                val picker = UIDocumentPickerViewController(
                    documentTypes = listOf("public.json", "public.plain-text"),
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeImport,
                )
                picker.delegate = delegate

                topMostController(rootController).presentViewController(
                    viewControllerToPresent = picker,
                    animated = true,
                    completion = null,
                )
            }
        }
    }
}

private class JsonDocumentPickerDelegate(
    private val onJsonPicked: (String) -> Unit,
    private val onPickFailed: (Throwable?) -> Unit,
    private val onFinished: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url == null) {
            onPickFailed(IllegalArgumentException("No file selected"))
            onFinished()
            return
        }

        runCatching {
            NSString.stringWithContentsOfURL(url, NSUTF8StringEncoding, null)
                ?: error("Unable to read selected file")
        }.onSuccess(onJsonPicked)
            .onFailure(onPickFailed)

        onFinished()
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onFinished()
    }
}

private fun topMostController(root: UIViewController): UIViewController {
    var current = root
    while (true) {
        val next = current.presentedViewController ?: break
        current = next
    }
    return current
}
