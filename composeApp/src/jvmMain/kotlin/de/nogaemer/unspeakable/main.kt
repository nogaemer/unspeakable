package de.nogaemer.unspeakable

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import de.nogaemer.unspeakable.navigation.RootComponent

fun main() = application {
    val lifecycle = LifecycleRegistry()
    val backDispatcher = BackDispatcher()
    val root = RootComponent(DefaultComponentContext(lifecycle, backHandler = backDispatcher))

    val windowState = rememberWindowState()

    LifecycleController(lifecycle, windowState)

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Unspeakable",
        onKeyEvent = { event ->
            if (event.key == Key.Escape && event.type == KeyEventType.KeyUp) {
                backDispatcher.back()
                true
            } else false
        }
    ) {
        App(root)
    }

}