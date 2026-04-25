package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual object WakeLock {
    actual fun acquire() {
    }

    actual fun release() {
    }
}

@Composable
actual fun KeepScreenOn() = Unit