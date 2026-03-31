package de.nogaemer.unspeakable

/**
 * Exposes runtime platform identity for diagnostics and UI labels.
 */
interface Platform {
    val name: String
}

/**
 * Resolves platform identity from target-specific implementations.
 * Android/iOS/JVM/JS/Wasm: each target returns its own runtime descriptor.
 */
expect fun getPlatform(): Platform