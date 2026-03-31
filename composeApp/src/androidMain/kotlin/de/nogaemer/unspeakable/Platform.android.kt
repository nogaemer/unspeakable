package de.nogaemer.unspeakable

import android.os.Build

/**
 * Reports Android runtime version for shared diagnostics and labels.
 */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

/**
 * Binds shared platform lookup to Android runtime metadata.
 * Android: uses SDK version from `Build.VERSION.SDK_INT`.
 */
actual fun getPlatform(): Platform = AndroidPlatform()
