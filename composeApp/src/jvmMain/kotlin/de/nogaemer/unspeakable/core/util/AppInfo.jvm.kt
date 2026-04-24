package de.nogaemer.unspeakable.core.util

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object AppInfo {
    actual val versionName: String
        get() = System.getProperty("app.version") ?: "dev"

    actual val versionCode: Int
        get() = System.getProperty("app.build")?.toInt() ?: 0
}