package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object WakeLock {
    fun acquire()
    fun release()
}

@Composable
expect fun KeepScreenOn()