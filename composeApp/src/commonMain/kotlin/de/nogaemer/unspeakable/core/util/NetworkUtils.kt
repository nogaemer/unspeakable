package de.nogaemer.unspeakable.core.util

/**
 * Resolves a local LAN-reachable IP address for multiplayer discovery and joining.
 * Android/JVM/iOS: each target uses its own network API strategy.
 */
expect fun getLocalIpAddress(): String?

