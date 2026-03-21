package de.nogaemer.unspeakable.features.game.preview

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.core.model.GameSettings
import de.nogaemer.unspeakable.core.model.Match
import de.nogaemer.unspeakable.core.model.PlayedCard
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.core.model.ProfileShape
import de.nogaemer.unspeakable.core.model.Round
import de.nogaemer.unspeakable.core.model.Team
import de.nogaemer.unspeakable.core.util.ImageUtils
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.features.game.GameState

/**
 * Reusable fixture states for Compose previews.
 */
object GameStatePreviewData {
    // Generate a valid dummy base64 image string to avoid rendering errors in previews
    private val dummyImage = run {
        val size = 16
        val bitmap = ImageBitmap(size, size)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { color = Color.Gray }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        ImageUtils.imageToBase64(bitmap)
    }

    private val hostPlayer = Player(
        id = "p_host",
        name = "Host",
        profilePicture = ProfilePicture(shape = ProfileShape.CIRCLE, image = dummyImage),
        isHost = true,
    )

    private val playerB = Player(
        id = "p_b",
        name = "Player 1",
        profilePicture = ProfilePicture(shape = ProfileShape.SQUARE, image = dummyImage),
        isHost = false,
    )

    private val playerC = Player(
        id = "p_c",
        name = "Player 2",
        profilePicture = ProfilePicture(shape = ProfileShape.HEART, image = dummyImage),
        isHost = false,
    )

    private val playerD = Player(
        id = "p_d",
        name = "Player 3",
        profilePicture = ProfilePicture(shape = ProfileShape.DIAMOND, image = dummyImage),
        isHost = false,
    )

    private val teamA = Team(
        id = "team_1",
        name = "Team A",
        players = listOf(hostPlayer, playerB),
        points = 7,
    )

    private val teamB = Team(
        id = "team_2",
        name = "Team B",
        players = listOf(playerC, playerD),
        points = 5,
    )

    private val previewMatch = Match(
        teams = listOf(teamA, teamB),
        players = listOf(hostPlayer, playerB, playerC, playerD),
        settings = GameSettings(
            roundTime = 60,
            maxRounds = 10,
        ),
    )

    private val previewCard = UnspeakableCard(
        id = 1,
        word = "Kotlin",
        category = "Programming",
        language = "en",
        forbidden1 = "JetBrains",
        forbidden2 = "JVM",
        forbidden3 = "Android",
        forbidden4 = "Coroutines",
        forbidden5 = "Compose",
    )

    private val previousCard = UnspeakableCard(
        id = 2,
        word = "Compiler",
        category = "Programming",
        language = "en",
        forbidden1 = "Code",
        forbidden2 = "Parse",
        forbidden3 = "Build",
        forbidden4 = "Kotlin",
        forbidden5 = "JVM",
    )

    private val previewCard2 = UnspeakableCard(
        id = 3,
        word = "Debugger",
        category = "Programming",
        language = "en",
        forbidden1 = "Breakpoints",
        forbidden2 = "Step",
        forbidden3 = "IDE",
        forbidden4 = "Error",
        forbidden5 = "Inspect",
    )

    private val previewCard3 = UnspeakableCard(
        id = 4,
        word = "Compiler",
        category = "Programming",
        language = "en",
        forbidden1 = "Code",
        forbidden2 = "Parse",
        forbidden3 = "Build",
        forbidden4 = "Kotlin",
        forbidden5 = "JVM",
    )

    private val previewCard4 = UnspeakableCard(
        id = 5,
        word = "House",
        category = "Animals",
        language = "en",
        forbidden1 = "Cat",
        forbidden2 = "Dog",
        forbidden3 = "Bird",
        forbidden4 = "Fish",
        forbidden5 = "Rabbit",
    )

    private val skippedCard = UnspeakableCard(
        id = 3,
        word = "Debugger",
        category = "Programming",
        language = "en",
        forbidden1 = "Breakpoints",
        forbidden2 = "Step",
        forbidden3 = "IDE",
        forbidden4 = "Error",
        forbidden5 = "Inspect",
    )

    private val previewPlayedCards = listOf(
        PlayedCard(card = previousCard, outcome = CardOutcome.CORRECT),
        PlayedCard(card = previewCard3, outcome = CardOutcome.CORRECT),
        PlayedCard(card = previewCard4, outcome = CardOutcome.SKIPPED),
        PlayedCard(card = skippedCard, outcome = CardOutcome.SKIPPED),
        PlayedCard(card = previewCard4, outcome = CardOutcome.SKIPPED),
        PlayedCard(card = previewCard4, outcome = CardOutcome.SKIPPED),
        PlayedCard(card = previewCard3, outcome = CardOutcome.SKIPPED),
        PlayedCard(card = skippedCard, outcome = CardOutcome.SKIPPED),
    )

    private val previewRound = Round(
        roundNumber = 3,
        explainerTeam = teamA,
        explainerPlayer = hostPlayer,
        playedCards = previewPlayedCards,
    )

    val lobby: GameState = GameState(
        phase = GamePhase.SETUP,
        isHost = true,
        me = hostPlayer,
        match = previewMatch,
    )

    val readyMyTurn: GameState = lobby.copy(
        phase = GamePhase.READY,
        currentRound = previewRound,
        currentRoundTime = 60,
    )

    val readyWaiting: GameState = lobby.copy(
        phase = GamePhase.READY,
        me = playerC,
        currentRound = previewRound,
        currentRoundTime = 60,
    )

    val playing: GameState = readyMyTurn.copy(
        phase = GamePhase.PLAYING,
        currentCard = previewCard,
        currentRoundTime = 42,
    )

    val roundSummary: GameState = lobby.copy(
        phase = GamePhase.ROUND_SUMMARY,
        rounds = listOf(previewRound),
        currentRound = previewRound.copy(
            playedCards = previewRound.playedCards + PlayedCard(
                card = previewCard,
                outcome = CardOutcome.CORRECT
            )
        ),
        currentCard = null,
        currentRoundTime = null,
    )
}
