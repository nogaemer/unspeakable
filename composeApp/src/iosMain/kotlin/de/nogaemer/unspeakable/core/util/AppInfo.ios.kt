package de.nogaemer.unspeakable.core.util

import platform.Foundation.NSBundle

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object AppInfo {
    actual val versionName: String
        get() = NSBundle.mainBundle
            .infoDictionary?.get("CFBundleShortVersionString") as? String ?: "dev"

    actual val versionCode: Int
        get() = (NSBundle.mainBundle
            .infoDictionary?.get("CFBundleVersion") as? String)?.toInt() ?: 0
}