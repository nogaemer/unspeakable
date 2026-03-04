package de.nogaemer.unspeakable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform