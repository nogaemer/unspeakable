package de.nogaemer.unspeakable

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsModule

/**
 * Declares timezone module side effects required by JS date-time handling.
 */
@OptIn(ExperimentalWasmJsInterop::class)
@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

/**
 * Boots the browser Compose viewport for the web target.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App()
    }
}