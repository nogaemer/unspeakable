package de.nogaemer.unspeakable.core.i18n

import cafe.adriel.lyricist.LyricistStrings
import de.nogaemer.unspeakable.core.util.settings.Locales

/** Holds generic labels reused across multiple screens. */
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

/** Holds navigation tab labels. */
data class NavigationStrings(
    val home: String,
    val words: String,
    val settings: String,
)

/** Holds text used on the home/start screen. */
data class HomeStrings(
    val hostAGame: String,
    val joinAGame: String,
    val local: String,
)

/** Holds labels and helper text for QR invite/join flows. */
data class QrStrings(
    val title: String,
    val scanToJoin: String,
    val description: String,
    val enterCode: String,
    val joinLobby: String,
)

/** Holds labels for name entry and lobby join setup. */
data class GameSetupStrings(
    val whatsYourName: String,
    val typePlaceholder: String,
    val playerName: String,
    val ipAddress: String,
    val startGame: String
)

/** Holds in-game team labels. */
data class GameStrings(
    val teamA: String,
    val teamB: String,
)

/** Holds text for the pre-round ready state. */
data class GameReadyStrings(
    val readyTitle: String,
    val readySubtitle: String,
    val waitingFor: String,
    val startTurnDescription: String,
)

/** Holds lobby labels for team assignment and game start. */
data class GameLobbyStrings(
    val lobbyTitle: String,
    val lobbySettingsDescription: String,
    val joinTeamDescription: String,
    val joinTeam: String,
    val startGame: String,
    val noPlayersInTeam: (String) -> String,
)

/** Holds lobby settings labels and nested round-setting strings. */
data class GameSettingsStrings(
    val lobbySettingsTitle: String,
    val roundsSettings: String,
    
    // Rounds per team navigation item
    val roundsPerTeam: String,
    val roundsPerTeamDescription: String,
    
    val gameRoundSettingsStrings: GameRoundSettingsStrings,
)

/** Holds strings for round-time and round-count configuration. */
data class GameRoundSettingsStrings(
    // Round time settings
    val roundTime: String,
    
    // Round count overview options
    val roundCountLittleLabel: String,
    val roundCountMiddleLabel: String,
    val roundCountManyLabel: String,
    val roundCountCustomLabel: String,
    
    // Custom round count settings page
    val customRoundsPageTitle: String,
    val customRoundsSliderLabel: String,
    
    // Helper formatting
    val roundCountValue: (Int) -> String,
)

/** Holds labels for post-round performance summaries. */
data class RoundOverviewStrings(
    val timeUpTitle: String,
    val correctLabel: String,
    val wrongLabel: String,
    val skippedLabel: String,
)

/** Holds copy shown when multiplayer connection drops. */
data class ConnectionLostStrings(
    val title: String,
    val description: String,
    val backToHome: String,
)

/** Holds copy shown on final game results and stats screens. */
data class GameOverStrings(
    val title: String,
    val subtitleWin: String,
    val winner: (String) -> String,
    val drawTitle: String,
    val overallStats: String,
    val timeStats: String,
    val totalTime: String,
    val pace: String,
    val matchStats: String,
    val roundsPlayed: String,
    val cardsPlayed: String,
    val correctLabel: String,
    val wrongLabel: String,
    val skippedLabel: String,
    val finalRanking: String,
    val pointsLabel: String,
    val winnerBadge: String,
    val participationBadge: String,
    val noValue: String,
    val durationFormat: (Int, Int) -> String,
    val paceFormat: (String) -> String,
    val backToHome: String,
)

/** Groups top-level settings section strings. */
data class SettingsStrings(
    val title: String,
    val personalizationStrings: SettingsPagePersonalizationStrings,
    val languageStrings: SettingsPageLanguageStrings,
    val aboutStrings: SettingsPageStrings
)

/** Defines common contract for settings subpage titles/descriptions. */
interface SettingsPageStrings {
    val title: String
    val description: String?
}

/** Holds personalization page labels for theme/color settings. */
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

/** Holds localized and translated names for one selectable language. */
data class LanguageEntry(
    val name: String,
    val translatedName: String,
)

/** Holds language page labels and available locale entries. */
data class SettingsPageLanguageStrings(
    override val title: String,
    override val description: String,
    val languages: Map<Locales, LanguageEntry>
) : SettingsPageStrings

/** Holds about page labels. */
data class SettingsPageAboutStrings(
    override val title: String,
    override val description: String? = null,
) : SettingsPageStrings


/** Root container for all localized string groups used by the app. */
data class Strings(
    val common: CommonStrings,
    val nav: NavigationStrings,
    val home: HomeStrings,
    val qr: QrStrings,
    val gameSetup: GameSetupStrings,
    val game: GameStrings,
    val gameReady: GameReadyStrings,
    val gameLobby: GameLobbyStrings,
    val gameLobbySettings: GameSettingsStrings,
    val roundOverview: RoundOverviewStrings,
    val connectionLost: ConnectionLostStrings,
    val gameOver: GameOverStrings,
    val settings: SettingsStrings,
    val gamePlaying: GamePlayingStrings,
)

data class GamePlayingStrings(
    val title: String,
    val subtitle: String,
)

/** English localization bundle used as default strings. */
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
        noPlayersInTeam = { teamName -> "No players in $teamName" },
    ),
    gameLobbySettings = GameSettingsStrings(
        lobbySettingsTitle = "Lobby Settings",
        roundsSettings = "Rounds Settings",
        roundsPerTeam = "Rounds per team",
        roundsPerTeamDescription = "Select the number of rounds per team",
        gameRoundSettingsStrings = GameRoundSettingsStrings(
            roundTime = "Round time",
            roundCountLittleLabel = "Little",
            roundCountMiddleLabel = "Middle",
            roundCountManyLabel = "Many",
            roundCountCustomLabel = "Custom",
            customRoundsPageTitle = "Rounds Count",
            customRoundsSliderLabel = "Number of Rounds",
            roundCountValue = { value -> "$value rounds" },
        )
    ),
    roundOverview = RoundOverviewStrings(
        timeUpTitle = "Time’s Up",
        correctLabel = "Correct",
        wrongLabel = "Wrong",
        skippedLabel = "Skipped",
    ),
    connectionLost = ConnectionLostStrings(
        title = "Connection lost",
        description = "Your connection to the host was interrupted.",
        backToHome = "Back to home",
    ),
    gameOver = GameOverStrings(
        title = "Game Over",
        subtitleWin = "Your Team won",
        winner = { teamName -> "$teamName wins" },
        drawTitle = "It's a draw",
        overallStats = "Overall stats",
        timeStats = "Time Stats",
        totalTime = "Total time",
        pace = "Pace",
        matchStats = "Match Stats",
        roundsPlayed = "Rounds played",
        cardsPlayed = "Cards played",
        correctLabel = "Correct",
        wrongLabel = "Wrong",
        skippedLabel = "Skipped",
        finalRanking = "Final ranking",
        pointsLabel = "pts",
        winnerBadge = "Winner!",
        participationBadge = "Well played",
        noValue = "-",
        durationFormat = { minutes, seconds ->
            "${minutes}m ${
                seconds.toString().padStart(2, '0')
            }s"
        },
        paceFormat = { value -> "$value s / card" },
        backToHome = "Back to home",
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
    gamePlaying = GamePlayingStrings(title = "GUESS!",
        subtitle = "find the target word"
    ),
)

/** German localization bundle. */
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
        noPlayersInTeam = { teamName -> "Keine Spieler in $teamName" },
    ),
    gameLobbySettings = GameSettingsStrings(
        lobbySettingsTitle = "Lobby-Einstellungen",
        roundsSettings = "Rundeneinstellungen",
        roundsPerTeam = "Runden pro Team",
        roundsPerTeamDescription = "Waehle die Anzahl der Runden pro Team",
        gameRoundSettingsStrings = GameRoundSettingsStrings(
            roundTime = "Rundenzeit",
            roundCountLittleLabel = "Wenig",
            roundCountMiddleLabel = "Mittel",
            roundCountManyLabel = "Viele",
            roundCountCustomLabel = "Benutzerdefiniert",
            customRoundsPageTitle = "Rundenanzahl",
            customRoundsSliderLabel = "Rundenanzahl",
            roundCountValue = { value -> "$value Runden" },
        )
    ),
    roundOverview = RoundOverviewStrings(
        timeUpTitle = "Zeit ist um",
        correctLabel = "Richtig",
        wrongLabel = "Falsch",
        skippedLabel = "Übersprungen",
    ),
    connectionLost = ConnectionLostStrings(
        title = "Verbindung verloren",
        description = "Die Verbindung zum Host wurde unterbrochen.",
        backToHome = "Zur Startseite",
    ),
    gameOver = GameOverStrings(
        title = "Spiel vorbei",
        subtitleWin = "Dein Team hat gewonnen",
        winner = { teamName -> "$teamName gewinnt" },
        drawTitle = "Unentschieden",
        overallStats = "Gesamtstatistik",
        timeStats = "Zeitstatistik",
        totalTime = "Gesamtzeit",
        pace = "Tempo",
        matchStats = "Spielstatistik",
        roundsPlayed = "Gespielte Runden",
        cardsPlayed = "Gespielte Karten",
        correctLabel = "Richtig",
        wrongLabel = "Falsch",
        skippedLabel = "Uebersprungen",
        finalRanking = "Endrangliste",
        pointsLabel = "Pkt",
        winnerBadge = "Gewinner!",
        participationBadge = "Gut gespielt",
        noValue = "-",
        durationFormat = { minutes, seconds ->
            "${minutes}m ${
                seconds.toString().padStart(2, '0')
            }s"
        },
        paceFormat = { value -> "$value s / Karte" },
        backToHome = "Zur Startseite",
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
    gamePlaying = GamePlayingStrings(title = "RATE!",
        subtitle = "errate das gesuchte Wort"
    ),
)
