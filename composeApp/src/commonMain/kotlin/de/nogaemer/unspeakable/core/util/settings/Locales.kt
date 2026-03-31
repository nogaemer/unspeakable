package de.nogaemer.unspeakable.core.util.settings

/**
 * Enumerates supported app locales and their persisted language tags.
 */
enum class Locales(val lang: String) {
    EN("en"),
    DE("de");

    companion object {
        fun from(lang: String) = entries.firstOrNull { it.lang == lang }
    }
}