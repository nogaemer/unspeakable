package de.nogaemer.unspeakable.core.util

/**
 * Exposes local LAN IP lookup for iOS callers.
 * iOS: currently returns null until a native implementation is wired.
 */
actual fun getLocalIpAddress(): String? {
    return null
}

