package de.nogaemer.unspeakable.core.util

import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Finds a non-loopback IPv4 address for Android LAN game flows.
 * Android: iterates `NetworkInterface` addresses and returns first IPv4 hit.
 */
actual fun getLocalIpAddress(): String? {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address is Inet4Address) {
                    return address.hostAddress
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

