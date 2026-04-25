package de.nogaemer.unspeakable.core.util

import android.content.Context
import android.net.wifi.WifiManager
import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import co.touchlab.kermit.Logger

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object WakeLock {
    private var cpuLock: PowerManager.WakeLock? = null
    private var wifiLock: WifiManager.WifiLock? = null

    fun init(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        cpuLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "unspeakable::GameSession"
        )

        val wm = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wm.createWifiLock(
            WifiManager.WIFI_MODE_FULL_LOW_LATENCY, // keeps radio fully active
            "unspeakable::GameSession"
        )
    }

    actual fun acquire() {
        cpuLock?.acquire(30 * 60 * 1000L)
        wifiLock?.acquire()
        Logger.d { "CPU lock held: ${cpuLock?.isHeld}, WiFi lock held: ${wifiLock?.isHeld}" }
    }

    actual fun release() {
        if (cpuLock?.isHeld == true) cpuLock?.release()
        if (wifiLock?.isHeld == true) wifiLock?.release()
    }
}

@Composable
actual fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}