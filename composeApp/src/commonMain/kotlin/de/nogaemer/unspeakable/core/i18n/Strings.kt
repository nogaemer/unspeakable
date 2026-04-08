package de.nogaemer.unspeakable.core.i18n

import cafe.adriel.lyricist.LyricistStrings
import de.nogaemer.unspeakable.core.mode.modes.SabotageMode
import de.nogaemer.unspeakable.core.mode.modes.SnowballMode
import de.nogaemer.unspeakable.core.mode.modes.SurvivalMode
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
    val cancel: String,
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
    val categoryStrings: Map<String, CategoryStrings>,
    val gameModesStrings: Map<String, GameModeStrings>,
)

interface GameModeStrings {
    val title: String
    val description: String
}

data class CategoryStrings(
    val title: String,
)

data class SabotageGameModeStrings(
    override val title: String,
    override val description: String,
    val sheetTitle: String,
    val sheetDescription: String,
    val wordPlaceholder: String,
    val wordCount: (Int, Int) -> String,
    val sendButtonText: String,
    val toastMessage: (String, String) -> String,
) : GameModeStrings

data class SurvivalGameModeStrings(
    override val title: String,
    override val description: String,
) : GameModeStrings

data class SnowballGameModeStrings(
    override val title: String,
    override val description: String,
) : GameModeStrings

/** Holds text for the pre-round ready state. */
data class GameReadyStrings(
    val readyTitle: String,
    val readySubtitle: String,
    val waitingFor: String,
    val startTurnDescription: String,
    val yourTurn: String,
)

/** Holds lobby labels for team assignment and game start. */
data class GameLobbyStrings(
    val lobbyTitle: String,
    val lobbySettingsDescription: String,
    val joinTeamDescription: String,
    val joinTeam: String,
    val startGame: String,
    val noPlayersInTeam: (String) -> String,
    val addPlayerTitle: (String) -> String,
    val nameLabel: String,
    val addButton: String,
    val moveToOtherTeamDescription: String,
    val addPlayerDescription: String,
)

/** Holds lobby settings labels and nested round-setting strings. */
data class GameSettingsStrings(
    val lobbySettingsTitle: String,
    val roundsSettings: String,

    val gameRoundSettingsStrings: GameRoundSettingsStrings,
    val gameModeSettingsStrings: GameModeSettingsStrings,
    val categoriesSettingsStrings: CategoriesSettingsStrings,
    val gameModeSettings: String,
)

/** Holds labels for lobby card-category settings. */
data class CategoriesSettingsStrings(
    val title: String,
    val description: String,
    val categoriesTitle: String,
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
    val title: String,
    val description: String,
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

data class SettingsPagePersonalizationPaletteStyleStrings(
    override val title: String,
    override val description: String,
): SettingsPageStrings

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
    val paletteStyleStrings: SettingsPagePersonalizationPaletteStyleStrings
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

@Suppress("unused")
fun Strings.categoryName(id: String): String = game.categoryStrings[id]?.title ?: id

data class GameModeSettingsStrings(
    val title: String,
    val description: String
)

private fun englishCategoryStrings() = mapOf(
    "adjectives" to CategoryStrings("Adjectives"),
    "animation" to CategoryStrings("Animations"),
    "art" to CategoryStrings("Art"),
    "body" to CategoryStrings("Body"),
    "clothing" to CategoryStrings("Clothing"),
    "entertainment" to CategoryStrings("Entertainment"),
    "environment" to CategoryStrings("Environment"),
    "events" to CategoryStrings("Events"),
    "food" to CategoryStrings("Food"),
    "games" to CategoryStrings("Games"),
    "geography" to CategoryStrings("Geography"),
    "health" to CategoryStrings("Health"),
    "history" to CategoryStrings("History"),
    "home" to CategoryStrings("Home"),
    "media" to CategoryStrings("Media"),
    "music" to CategoryStrings("Music"),
    "nature" to CategoryStrings("Nature"),
    "objects" to CategoryStrings("Objects"),
    "people_and_feelings" to CategoryStrings("People & Feelings"),
    "people_and_jobs" to CategoryStrings("People & Jobs"),
    "places" to CategoryStrings("Places"),
    "politics" to CategoryStrings("Politics"),
    "religion" to CategoryStrings("Religion"),
    "school_and_work" to CategoryStrings("School & Work"),
    "sports_and_games" to CategoryStrings("Sports & Games"),
    "technology" to CategoryStrings("Technology"),
    "travel" to CategoryStrings("Travel"),
    "verbs" to CategoryStrings("Verbs"),
    "weather" to CategoryStrings("Weather"),
)

private fun germanCategoryStrings() = mapOf(
    "adjectives" to CategoryStrings("Adjektive"),
    "animations" to CategoryStrings("Animationen"),
    "art" to CategoryStrings("Kunst"),
    "body" to CategoryStrings("Körper"),
    "clothing" to CategoryStrings("Kleidung"),
    "entertainment" to CategoryStrings("Unterhaltung"),
    "environment" to CategoryStrings("Umwelt"),
    "events" to CategoryStrings("Ereignisse"),
    "food" to CategoryStrings("Essen"),
    "games" to CategoryStrings("Spiele"),
    "geography" to CategoryStrings("Geographie"),
    "health" to CategoryStrings("Gesundheit"),
    "history" to CategoryStrings("Geschichte"),
    "home" to CategoryStrings("Zuhause"),
    "media" to CategoryStrings("Medien"),
    "music" to CategoryStrings("Musik"),
    "nature" to CategoryStrings("Natur"),
    "objects" to CategoryStrings("Gegenstände"),
    "people_and_feelings" to CategoryStrings("Menschen & Gefühle"),
    "people_and_jobs" to CategoryStrings("Menschen & Berufe"),
    "places" to CategoryStrings("Orte"),
    "politics" to CategoryStrings("Politik"),
    "religion" to CategoryStrings("Religion"),
    "school_and_work" to CategoryStrings("Schule & Arbeit"),
    "sports_and_games" to CategoryStrings("Sport & Spiele"),
    "technology" to CategoryStrings("Technologie"),
    "travel" to CategoryStrings("Reisen"),
    "verbs" to CategoryStrings("Verben"),
    "weather" to CategoryStrings("Wetter"),
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
        cancel = "Cancel",
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
        categoryStrings = englishCategoryStrings(),
        gameModesStrings = mapOf(
            SabotageMode().id to SabotageGameModeStrings(
                title = "Sabotage",
                description = "Sabotage the other team by injecting new forbidden words into their cards.",
                sheetTitle = "Sabotage",
                sheetDescription = "Enter a word for your opponent",
                wordPlaceholder = "Word",
                wordCount = { currentLength, limit -> "$currentLength/$limit" },
                sendButtonText = "Send sabotage",
                toastMessage = { playerName, word -> "$playerName added $word" },
            ),
            SurvivalMode().id to SurvivalGameModeStrings(
                title = "Survival",
                description = "Survive as long as possible under time pressure."
            ),
            SnowballMode().id to SnowballGameModeStrings(
                title = "Bluff Cards",
                description = "Every time you guess a word correctly, it becomes a new forbidden word."
            )
        )
    ),
    gameReady = GameReadyStrings(
        readyTitle = "READY ?",
        readySubtitle = "start your turn",
        waitingFor = "Waiting for",
        startTurnDescription = "Start turn",
        yourTurn = "Your turn is about to start,",
    ),
    gameLobby = GameLobbyStrings(
        lobbyTitle = "Lobby",
        lobbySettingsDescription = "Lobby settings",
        joinTeamDescription = "Join team",
        joinTeam = "Join Team",
        startGame = "Start Game",
        noPlayersInTeam = { teamName -> "No players in $teamName" },
        addPlayerTitle = { teamName -> "Add player to $teamName" },
        nameLabel = "Name",
        addButton = "Add",
        moveToOtherTeamDescription = "Move to other team",
        addPlayerDescription = "Add player",
    ),
    gameLobbySettings = GameSettingsStrings(
        lobbySettingsTitle = "Lobby Settings",
        roundsSettings = "Rounds Settings",
        gameRoundSettingsStrings = GameRoundSettingsStrings(
            roundTime = "Round time",
            roundCountLittleLabel = "Little",
            roundCountMiddleLabel = "Middle",
            roundCountManyLabel = "Many",
            roundCountCustomLabel = "Custom",
            title = "Rounds per team",
            description = "Choose the number of rounds per team",
            customRoundsSliderLabel = "Number of Rounds",
            roundCountValue = { value -> "$value rounds" },
        ),
        gameModeSettingsStrings = GameModeSettingsStrings(
            title = "Game Mode",
            description = "Choose mode — adjust core rules"
        ),
        categoriesSettingsStrings = CategoriesSettingsStrings(
            title = "Cards",
            description = "Choose which categories are used in the game",
            categoriesTitle = "Categories",
        ),
        gameModeSettings = "Game Rules"
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
            paletteStyleStrings = SettingsPagePersonalizationPaletteStyleStrings(
                title = "Palette Style",
                description = "Choose a color palette for the app",
            )
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
    gamePlaying = GamePlayingStrings(
        title = "GUESS!",
        subtitle = "find the target word",
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
        cancel = "Abbrechen",
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
        categoryStrings = germanCategoryStrings(),
        gameModesStrings = mapOf(
            SabotageMode().id to SabotageGameModeStrings(
                title = "Sabotage",
                description = "Sabotiere das gegnerische Team, indem du neue verbotene Wörter in ihre Karten einschleust.",
                sheetTitle = "Sabotage",
                sheetDescription = "Gib deinem Gegner ein Wort vor",
                wordPlaceholder = "Wort",
                wordCount = { currentLength, limit -> "$currentLength/$limit" },
                sendButtonText = "Sabotage senden",
                toastMessage = { playerName, word -> "$playerName hat $word hinzugefügt" },
            ),
            SurvivalMode().id to SurvivalGameModeStrings(
                title = "Survival",
                description = "Überlebe so lange wie möglich unter Zeitdruck."
            ),
            SnowballMode().id to SnowballGameModeStrings(
                title = "Bluff-Karten",
                description = "Jedes Mal, wenn du ein Wort richtig errätst, wird es zu einem neuen verbotenen Wort. "
            )
        )
    ),
    gameReady = GameReadyStrings(
        readyTitle = "BEREIT?",
        readySubtitle = "Starte deinen Zug",
        waitingFor = "Warten auf",
        startTurnDescription = "Zug starten",
        yourTurn = "Dein Zug started gleich,",
    ),
    gameLobby = GameLobbyStrings(
        lobbyTitle = "Lobby",
        lobbySettingsDescription = "Lobby-Einstellungen",
        joinTeamDescription = "Team beitreten",
        joinTeam = "beitreten",
        startGame = "Spiel starten",
        noPlayersInTeam = { teamName -> "Keine Spieler in $teamName" },
        addPlayerTitle = { teamName -> "Spieler zu $teamName hinzufügen" },
        nameLabel = "Name",
        addButton = "Hinzufügen",
        moveToOtherTeamDescription = "In anderes Team verschieben",
        addPlayerDescription = "Spieler hinzufügen",
    ),
    gameLobbySettings = GameSettingsStrings(
        lobbySettingsTitle = "Lobby-Einstellungen",
        roundsSettings = "Rundeneinstellungen",
        gameRoundSettingsStrings = GameRoundSettingsStrings(
            roundTime = "Rundenzeit",
            roundCountLittleLabel = "Wenig",
            roundCountMiddleLabel = "Mittel",
            roundCountManyLabel = "Viele",
            roundCountCustomLabel = "Benutzerdefiniert",
            title = "Rundenanzahl",
            description = "Wähle die Anzahl der Runden pro Team",
            customRoundsSliderLabel = "Rundenanzahl",
            roundCountValue = { value -> "$value Runden" },
        ),
        gameModeSettingsStrings = GameModeSettingsStrings(
            title = "Spielmodus",
            description = "Spielmodus wählen — Spielregeln anpassen"
        ),
        categoriesSettingsStrings = CategoriesSettingsStrings(
            title = "Karten",
            description = "Wähle, welche Kategorien im Spiel verwendet werden",
            categoriesTitle = "Kategorien",
        ),
        gameModeSettings = "Spielregeln"
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
        skippedLabel = "Übersprungen",
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
            paletteStyleStrings = SettingsPagePersonalizationPaletteStyleStrings(
                title = "Palette-Stil",
                description = "Wähle eine Farbpalette für die App",
            )
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
    gamePlaying = GamePlayingStrings(
        title = "RATE!",
        subtitle = "errate das gesuchte Wort",
    ),
)
