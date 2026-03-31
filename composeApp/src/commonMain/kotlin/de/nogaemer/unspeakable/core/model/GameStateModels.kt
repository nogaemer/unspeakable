package de.nogaemer.unspeakable.core.model

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon
import de.nogaemer.unspeakable.db.UnspeakableCard
import kotlinx.serialization.Serializable

/**
 * Represents a team roster and score within the current match.
 */
@Serializable
data class Team(
    val id: String,
    val name: String,
    val players: List<Player>,
    val points: Int = 0,
)

/**
 * Represents full lobby composition and active game settings.
 */
@Serializable
data class Match(
    val teams: List<Team>,
    val players: List<Player>,
    val settings: GameSettings = GameSettings(),
)

/**
 * Holds tunable settings synchronized across all participants.
 */
@Serializable
data class GameSettings(
    val roundTime: Int = 60,
    val maxRounds: Int = 10,
)

@Serializable
enum class CardOutcome { CORRECT, SKIPPED, WRONG }

@Serializable
data class PlayedCard(
    val card: UnspeakableCard,
    val outcome: CardOutcome,
)

/**
 * Captures immutable state and outcomes for a single round.
 */
@Serializable
data class Round(
    val roundNumber: Int,
    val explainerTeam: Team,
    val explainerPlayer: Player,
    val playedCards: List<PlayedCard> = emptyList(),
    val durationSeconds: Int = 0,
) {
    val correct: Int get() = playedCards.count { it.outcome == CardOutcome.CORRECT }
    val skipped: Int get() = playedCards.count { it.outcome == CardOutcome.SKIPPED }
    val wrong:   Int get() = playedCards.count { it.outcome == CardOutcome.WRONG }
    val points:  Int get() = correct
}

@Serializable
data class Player(
    val id: String,
    val name: String,
    val profilePicture: ProfilePicture,
    val isHost: Boolean,
)

@Serializable
data class ProfilePicture(
    val shape: ProfileShape,
    val image: String,
)

@Serializable
enum class GamePhase {
    SETUP,
    READY,
    PLAYING,
    ROUND_SUMMARY,
    GAME_OVER,
    CONNECTION_LOST,
}

enum class NetworkMode { LAN, LOCAL_DEVICE }


enum class ProfileShape {
    CIRCLE,
    SQUARE,
    SLANTED,
    ARCH,
    FAN,
    ARROW,
    SEMI_CIRCLE,
    OVAL,
    PILL,
    TRIANGLE,
    DIAMOND,
    CLAM_SHELL,
    PENTAGON,
    GEM,
    SUNNY,
    VERY_SUNNY,
    COOKIE_4_SIDED,
    COOKIE_6_SIDED,
    COOKIE_7_SIDED,
    COOKIE_9_SIDED,
    COOKIE_12_SIDED,
    GHOSTISH,
    CLOVER_4_LEAF,
    CLOVER_8_LEAF,
    BURST,
    SOFT_BURST,
    BOOM,
    SOFT_BOOM,
    FLOWER,
    PUFFY,
    PUFFY_DIAMOND,
    PIXEL_CIRCLE,
    PIXEL_TRIANGLE,
    BUN,
    HEART,
}

/**
 * Maps profile-shape presets to Material rounded polygons for avatar rendering.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun ProfileShape.toRoundedPolygon(): RoundedPolygon = when (this) {
    ProfileShape.CIRCLE -> MaterialShapes.Circle
    ProfileShape.SQUARE -> MaterialShapes.Square
    ProfileShape.SLANTED -> MaterialShapes.Slanted
    ProfileShape.ARCH -> MaterialShapes.Arch
    ProfileShape.FAN -> MaterialShapes.Fan
    ProfileShape.ARROW -> MaterialShapes.Arrow
    ProfileShape.SEMI_CIRCLE -> MaterialShapes.SemiCircle
    ProfileShape.OVAL -> MaterialShapes.Oval
    ProfileShape.PILL -> MaterialShapes.Pill
    ProfileShape.TRIANGLE -> MaterialShapes.Triangle
    ProfileShape.DIAMOND -> MaterialShapes.Diamond
    ProfileShape.CLAM_SHELL -> MaterialShapes.ClamShell
    ProfileShape.PENTAGON -> MaterialShapes.Pentagon
    ProfileShape.GEM -> MaterialShapes.Gem
    ProfileShape.SUNNY -> MaterialShapes.Sunny
    ProfileShape.VERY_SUNNY -> MaterialShapes.VerySunny
    ProfileShape.COOKIE_4_SIDED -> MaterialShapes.Cookie4Sided
    ProfileShape.COOKIE_6_SIDED -> MaterialShapes.Cookie6Sided
    ProfileShape.COOKIE_7_SIDED -> MaterialShapes.Cookie7Sided
    ProfileShape.COOKIE_9_SIDED -> MaterialShapes.Cookie9Sided
    ProfileShape.COOKIE_12_SIDED -> MaterialShapes.Cookie12Sided
    ProfileShape.GHOSTISH -> MaterialShapes.Ghostish
    ProfileShape.CLOVER_4_LEAF -> MaterialShapes.Clover4Leaf
    ProfileShape.CLOVER_8_LEAF -> MaterialShapes.Clover8Leaf
    ProfileShape.BURST -> MaterialShapes.Burst
    ProfileShape.SOFT_BURST -> MaterialShapes.SoftBurst
    ProfileShape.BOOM -> MaterialShapes.Boom
    ProfileShape.SOFT_BOOM -> MaterialShapes.SoftBoom
    ProfileShape.FLOWER -> MaterialShapes.Flower
    ProfileShape.PUFFY -> MaterialShapes.Puffy
    ProfileShape.PUFFY_DIAMOND -> MaterialShapes.PuffyDiamond
    ProfileShape.PIXEL_CIRCLE -> MaterialShapes.PixelCircle
    ProfileShape.PIXEL_TRIANGLE -> MaterialShapes.PixelTriangle
    ProfileShape.BUN -> MaterialShapes.Bun
    ProfileShape.HEART -> MaterialShapes.Heart
}
