package de.nogaemer.unspeakable.core.i18n

import cafe.adriel.lyricist.LyricistStrings
import de.nogaemer.unspeakable.core.util.settings.Locales

// ── Groups ────────────────────────────────────────────────────────────────────

data class CommonStrings(
    val appName: String,
    val appTagline: String,
    val start: String,
    val back: String,
    val or: String,
    val copy: String,
    val share: String,
    val next: String,
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

data class QrStrings(
    val title: String,
    val scanToJoin: String,
    val description: String,
    val enterCode: String,
    val joinLobby: String,
)

data class GameSetupStrings(
    val whatsYourName: String,
    val typePlaceholder: String,
    val playerName: String,
    val ipAddress: String,
    val startGame: String
)

data class GameStrings(
    val teamA: String,
    val teamB: String,
)

data class GameReadyStrings(
    val readyTitle: String,
    val readySubtitle: String,
    val waitingFor: String,
    val startTurnDescription: String,
)

data class GameLobbyStrings(
    val lobbyTitle: String,
    val lobbySettingsDescription: String,
    val joinTeamDescription: String,
    val joinTeam: String,
    val startGame: String,
)

data class GameLobbySettingsStrings(
    val lobbySettingsTitle: String,
    val roundsSettings: String,
    val roundTime: String,
    val roundsPerTeam: String,
    val roundsPerTeamDescription: String,
)

data class RoundOverviewStrings(
    val timeUpTitle: String,
    val correctLabel: String,
    val wrongLabel: String,
    val skippedLabel: String,
)

data class SettingsStrings(
    val title: String,
    val personalizationStrings: SettingsPagePersonalizationStrings,
    val languageStrings: SettingsPageLanguageStrings,
    val aboutStrings: SettingsPageStrings
)

interface SettingsPageStrings {
    val title: String
    val description: String?
}

data class SettingsPagePersonalizationStrings(
    override val title: String,
    override val description: String,
    val themeSectionTitle: String,
    val systemModeLabel: String,
    val lightModeLabel: String,
    val darkModeLabel: String,
    val amoledLabel: String,
    val dynamicColorLabel: String,
    val dynamicColorDescription: String,
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

data class SettingsPageAboutStrings(
    override val title: String,
    override val description: String? = null,
) : SettingsPageStrings



// ── Root ──────────────────────────────────────────────────────────────────────

data class Strings(
    val common: CommonStrings,
    val nav: NavigationStrings,
    val home: HomeStrings,
    val qr: QrStrings,
    val gameSetup: GameSetupStrings,
    val game: GameStrings,
    val gameReady: GameReadyStrings,
    val gameLobby: GameLobbyStrings,
    val gameLobbySettings: GameLobbySettingsStrings,
    val roundOverview: RoundOverviewStrings,
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
        or = "or",
        copy = "Copy",
        share = "Share",
        next = "Next",
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
    qr = QrStrings(
        title = "Invite Players",
        scanToJoin = "Scan to Join",
        description = "Scan the QR to join friends in this game lobby.",
        enterCode = "Enter Code",
        joinLobby = "Join Lobby",
    ),
    gameSetup = GameSetupStrings(
        whatsYourName = "What's your name?",
        typePlaceholder = "Type",
        playerName = "Player Name",
        ipAddress = "IP Address",
        startGame = "Start Game",
    ),
    game = GameStrings(
        teamA = "Team A",
        teamB = "Team B",
    ),
    gameReady = GameReadyStrings(
        readyTitle = "READY ?",
        readySubtitle = "start your turn",
        waitingFor = "Waiting for",
        startTurnDescription = "Start turn",
    ),
    gameLobby = GameLobbyStrings(
        lobbyTitle = "Lobby",
        lobbySettingsDescription = "Lobby settings",
        joinTeamDescription = "Join team",
        joinTeam = "Join Team",
        startGame = "Start Game",
    ),
    gameLobbySettings = GameLobbySettingsStrings(
        lobbySettingsTitle = "Lobby Settings",
        roundsSettings = "Rounds Settings",
        roundTime = "Round time",
        roundsPerTeam = "Rounds per team",
        roundsPerTeamDescription = "Select the number of rounds per team",
    ),
    roundOverview = RoundOverviewStrings(
        timeUpTitle = "Time’s Up",
        correctLabel = "Correct",
        wrongLabel = "Wrong",
        skippedLabel = "Skipped",
    ),
    settings = SettingsStrings(
        title = "Settings",
        personalizationStrings = SettingsPagePersonalizationStrings(
            title = "Personalization",
            description = "Change the app theme and colors",
            themeSectionTitle = "Theme",
            systemModeLabel = "System",
            lightModeLabel = "Light",
            darkModeLabel = "Dark",
            amoledLabel = "Amoled",
            dynamicColorLabel = "Dynamic Color",
            dynamicColorDescription = "Change the color of the app based on the wallpaper",
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
        ),
        aboutStrings = SettingsPageAboutStrings(
            title = "About",
        ),
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
        or = "oder",
        copy = "Kopieren",
        share = "Teilen",
        next = "Weiter",
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
    qr = QrStrings(
        title = "Spieler einladen",
        scanToJoin = "QR scannen",
        description = "Scan den QR-Code, um Freunden in dieser Spiel-Lobby beizutreten",
        enterCode = "Code eingeben",
        joinLobby = "Lobby beitreten",
    ),
    gameSetup = GameSetupStrings(
        whatsYourName = "Wie heißt du?",
        typePlaceholder = "Tippen",
        playerName = "Spielername",
        ipAddress = "IP-Adresse",
        startGame = "Spiel starten",
    ),
    game = GameStrings(
        teamA = "Team A",
        teamB = "Team B",
    ),
    gameReady = GameReadyStrings(
        readyTitle = "BEREIT?",
        readySubtitle = "Starte deinen Zug",
        waitingFor = "Warten auf",
        startTurnDescription = "Zug starten",
    ),
    gameLobby = GameLobbyStrings(
        lobbyTitle = "Lobby",
        lobbySettingsDescription = "Lobby-Einstellungen",
        joinTeamDescription = "Team beitreten",
        joinTeam = "beitreten",
        startGame = "Spiel starten",
    ),
    gameLobbySettings = GameLobbySettingsStrings(
        lobbySettingsTitle = "Lobby-Einstellungen",
        roundsSettings = "Rundeneinstellungen",
        roundTime = "Rundenzeit",
        roundsPerTeam = "Runden pro Team",
        roundsPerTeamDescription = "Waehle die Anzahl der Runden pro Team",
    ),
    roundOverview = RoundOverviewStrings(
        timeUpTitle = "Zeit ist um",
        correctLabel = "Richtig",
        wrongLabel = "Falsch",
        skippedLabel = "Übersprungen",
    ),
    settings = SettingsStrings(
        title = "Einstellungen",
        personalizationStrings = SettingsPagePersonalizationStrings(
            title = "Personalisierung",
            description = "Ändern Sie das App-Thema und Farben",
            themeSectionTitle = "Thema",
            systemModeLabel = "System",
            lightModeLabel = "Hell",
            darkModeLabel = "Dunkel",
            amoledLabel = "Amoled",
            dynamicColorLabel = "Dynamische Farben",
            dynamicColorDescription = "Ändert die App-Farbe basierend auf dem Hintergrundbild",
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
        aboutStrings = SettingsPageAboutStrings(
            title = "Über",
        ),
    ),
)
