package de.nogaemer.unspeakable.core.i18n

import cafe.adriel.lyricist.LyricistStrings
import de.nogaemer.unspeakable.core.util.settings.Locales

// ── Groups ────────────────────────────────────────────────────────────────────

data class CommonStrings(
    val appName: String,
    val appTagline: String,
    val start: String,
    val back: String,
)

data class NavigationStrings(
    val home: String,
    val words: String,
    val settings: String,
)

data class HomeStrings(
    val hostAGame: String,
    val joinAGame: String,
    val local: String,
)

data class GameSetupStrings(
    val playerName: String,
    val ipAddress: String,
    val startGame: String
)

data class GameStrings(
    val teamA: String,
    val teamB: String,
)

data class SettingsStrings(
    val title: String,
    val aboutStrings: SettingsPageStrings,
    val languageStrings: SettingsPageLanguageStrings
)

interface SettingsPageStrings {
    val title: String
    val description: String?
}

data class SettingsPageAboutStrings(
    override val title: String,
    override val description: String? = null,
) : SettingsPageStrings

data class LanguageEntry(
    val name: String,
    val translatedName: String,
)
data class SettingsPageLanguageStrings(
    override val title: String,
    override val description: String,
    val languages: Map<Locales, LanguageEntry>
): SettingsPageStrings


// ── Root ──────────────────────────────────────────────────────────────────────

data class Strings(
    val common: CommonStrings,
    val nav: NavigationStrings,
    val home: HomeStrings,
    val gameSetup: GameSetupStrings,
    val game: GameStrings,
    val settings: SettingsStrings,
)

// ── English ───────────────────────────────────────────────────────────────────

@LyricistStrings(languageTag = "en", default = true)
val EnStrings = Strings(
    common = CommonStrings(
        appName = "Unspeakable",
        appTagline = "THE ULTIMATE WORD GAME",
        start = "Start",
        back = "Back",
    ),
    nav = NavigationStrings(
        home = "Home",
        words = "Words",
        settings = "Settings",
    ),
    home = HomeStrings(
        hostAGame = "Host  a\nGame",
        joinAGame = "Join  a\nGame",
        local = "Local",
    ),
    gameSetup = GameSetupStrings(
        playerName = "Player Name",
        ipAddress = "IP Address",
        startGame = "Start Game",
    ),
    game = GameStrings(
        teamA = "Team A",
        teamB = "Team B",
    ),
    settings = SettingsStrings(
        title = "Settings",
        aboutStrings = SettingsPageAboutStrings(
            title = "About",
        ),
        languageStrings = SettingsPageLanguageStrings(
            title = "Language",
            description = "Change the app language",
            languages = mapOf(
                Locales.EN to LanguageEntry(
                    name = "English",
                    translatedName = "English",
                ),
                Locales.DE to LanguageEntry(
                    name = "Deutsch",
                    translatedName = "German",
                )
            )
        )
    ),
)

// ── German ────────────────────────────────────────────────────────────────────

@LyricistStrings(languageTag = "de")
val DeStrings = Strings(
    common = CommonStrings(
        appName = "Unspeakable",
        appTagline = "DAS ULTIMATIVE WORTSPIEL",
        start = "Starten",
        back = "Zurück",
    ),
    nav = NavigationStrings(
        home = "Start",
        words = "Wörter",
        settings = "Einstellungen",
    ),
    home = HomeStrings(
        hostAGame = "Spiel\nhosten",
        joinAGame = "Spiel\nbeitreten",
        local = "Lokal",
    ),
    gameSetup = GameSetupStrings(
        playerName = "Spielername",
        ipAddress = "IP-Adresse",
        startGame = "Spiel starten",
    ),
    game = GameStrings(
        teamA = "Team A",
        teamB = "Team B",
    ),
    settings = SettingsStrings(
        title = "Einstellungen",
        aboutStrings = SettingsPageAboutStrings(
            title = "Über",
        ),
        languageStrings = SettingsPageLanguageStrings(
            title = "Sprache",
            description = "App-Sprache ändern",
            languages = mapOf(
                Locales.EN to LanguageEntry(
                    name = "English",
                    translatedName = "Englisch",
                ),
                Locales.DE to LanguageEntry(
                    name = "Deutsch",
                    translatedName = "Deutsch",
                )
            )
        ),
    ),
)
