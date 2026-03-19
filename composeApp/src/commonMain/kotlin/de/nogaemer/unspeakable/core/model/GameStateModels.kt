package de.nogaemer.unspeakable.core.model

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon
import de.nogaemer.unspeakable.db.UnspeakableCard
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val name: String,
    val players: List<Player>,
    val points: Int,
    val cards: List<UnspeakableCard>,
)

@Serializable
data class Match(
    val teams: List<Team>,
    val players: List<Player>,
    val settings: GameSettings = GameSettings(),
)

@Serializable
data class GameSettings(
    val roundTime: Int = 60,
    val maxRounds: Int = 10,
)

enum class NetworkMode {
    LAN ,
    LOCAL_DEVICE ,
}

@Serializable
data class Round(
    val roundNumber: Int,
    val explainerTeam: Team,
    val cards: List<UnspeakableCard>,
    val points: Int,
)

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

@Serializable
enum class GamePhase {
    SETUP,
    READY,
    PLAYING,
    ROUND_SUMMARY,
    GAME_OVER
}
