package de.nogaemer.unspeakable.core.util

import de.nogaemer.unspeakable.BuildConfig



@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object AppInfo {
    actual val versionName: String
        get() = BuildConfig.VERSION_NAME

    actual val versionCode: Int
        get() = BuildConfig.VERSION_CODE
}