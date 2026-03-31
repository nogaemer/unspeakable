package de.nogaemer.unspeakable

/**
 * Reports Kotlin/JS runtime identity for web diagnostics.
 */
class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

/**
 * Binds shared platform lookup to the Kotlin/JS web label.
 * JS: returns a browser-target runtime descriptor.
 */
actual fun getPlatform(): Platform = JsPlatform()