package de.nogaemer.unspeakable.core.util

import java.net.NetworkInterface

/**
 * Selects a LAN-reachable IPv4 address for desktop multiplayer flows.
 * JVM: filters loopback/IPv6 and keeps site-local addresses only.
 */
actual fun getLocalIpAddress(): String? {
    return try {
        NetworkInterface.getNetworkInterfaces().asSequence()
            .flatMap { it.inetAddresses.asSequence() }
            .filter { !it.isLoopbackAddress && it.isSiteLocalAddress && !it.hostAddress.contains(":") }
            .map { it.hostAddress }
            .firstOrNull()
    } catch (e: Exception) {
        null
    }
}

