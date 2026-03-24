package de.nogaemer.unspeakable.core.util

import java.net.NetworkInterface

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

