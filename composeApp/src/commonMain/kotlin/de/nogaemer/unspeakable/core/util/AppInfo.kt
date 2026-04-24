package de.nogaemer.unspeakable.core.util

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppInfo {
    val versionName: String
    val versionCode: Int
}