package de.nogaemer.unspeakable

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import de.nogaemer.unspeakable.navigation.RootComponent

fun main() = application {
    val lifecycle = LifecycleRegistry()

    val root = RootComponent(DefaultComponentContext(lifecycle))

    val windowState = rememberWindowState()

    LifecycleController(lifecycle, windowState)

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Unspeakable"
    ) {
        App(root)
    }

}