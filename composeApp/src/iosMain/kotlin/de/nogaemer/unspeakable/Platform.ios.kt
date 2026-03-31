package de.nogaemer.unspeakable

import platform.UIKit.UIDevice

/**
 * Reports iOS runtime name/version for shared diagnostics and labels.
 */
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

/**
 * Binds shared platform lookup to UIKit device metadata.
 * iOS: reads system name/version from `UIDevice.currentDevice`.
 */
actual fun getPlatform(): Platform = IOSPlatform()
