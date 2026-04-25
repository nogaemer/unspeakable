package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object WakeLock {
    actual fun acquire() {
        UIApplication.sharedApplication.idleTimerDisabled = true
    }
    actual fun release() {
        UIApplication.sharedApplication.idleTimerDisabled = false
    }
}

@Composable
actual fun KeepScreenOn() {
    DisposableEffect(Unit) {
        UIApplication.sharedApplication.idleTimerDisabled = true
        onDispose { UIApplication.sharedApplication.idleTimerDisabled = false }
    }
}