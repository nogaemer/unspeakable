package de.nogaemer.unspeakable.features.game.phases.gameover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleGauge
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SkipForward
import com.composables.icons.lucide.X
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.i18n.GameOverStrings
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.Team
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexTitleVariation
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData
import kotlin.math.roundToInt

private data class GameOverStats(
    val roundsPlayed: Int,
    val totalCards: Int,
    val correctCards: Int,
    val wrongCards: Int,
    val skippedCards: Int,
    val rankedTeams: List<Team>,
    val winnerTeamName: String?,
    val isDraw: Boolean,
    val totalTimeSeconds: Int,
    val secondsPerCard: Double?,
)

/**
 * Shows final ranking and match statistics at game completion.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GameOverScreen(
    state: GameState,
    onGoHome: () -> Unit,
) {
    val text = strings.gameOver
    val gameText = strings.game
    val stats = state.toGameOverStats()

    val leftTeam = stats.rankedTeams.getOrNull(0)
    val rightTeam = stats.rankedTeams.getOrNull(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            text = text.title,
            style = TextStyle(
                fontSize = 72.sp,
                fontFamily = robotoFlex(robotoFlexTitleVariation()),
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = if (stats.isDraw) text.drawTitle else text.subtitleWin,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            TeamScoreCard(
                modifier = Modifier.weight(1f),
                teamName = leftTeam?.name ?: gameText.teamA,
                points = leftTeam?.points ?: 0,
                isWinner = leftTeam != null && rightTeam != null && leftTeam.points > rightTeam.points,
                isDraw = stats.isDraw,
                winnerLabel = text.winnerBadge,
                participationLabel = text.participationBadge,
            )
            TeamScoreCard(
                modifier = Modifier.weight(1f),
                teamName = rightTeam?.name ?: gameText.teamB,
                points = rightTeam?.points ?: 0,
                isWinner = rightTeam != null && leftTeam != null && rightTeam.points > leftTeam.points,
                isDraw = stats.isDraw,
                winnerLabel = text.winnerBadge,
                participationLabel = text.participationBadge,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SegmentedColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            segmentTitle = text.timeStats,
        ) {
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.totalTime, formatDuration(stats.totalTimeSeconds, text), Lucide.Clock)
                },
            )
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.pace, formatPace(stats.secondsPerCard, text), Lucide.CircleGauge)
                },
            )
        }

        SegmentedColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            segmentTitle = text.matchStats,
        ) {
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.roundsPlayed, stats.roundsPlayed.toString(), Lucide.BookOpen)
                },
            )
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.cardsPlayed, stats.totalCards.toString(), Lucide.Info)
                },
            )
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.correctLabel, stats.correctCards.toString(), Lucide.Check)
                },
            )
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.wrongLabel, stats.wrongCards.toString(), Lucide.X)
                },
            )
            SegmentedListItem(
                headlineContent = {
                    StatsRow(text.skippedLabel, stats.skippedCards.toString(), Lucide.SkipForward)
                },
            )
        }


        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onGoHome,
            shapes = ButtonDefaults.shapesFor(ButtonDefaults.LargeContainerHeight),
            contentPadding = ButtonDefaults.LargeContentPadding,
        ) {
            Text(text = text.backToHome, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun TeamScoreCard(
    modifier: Modifier = Modifier,
    teamName: String,
    points: Int,
    isWinner: Boolean,
    isDraw: Boolean,
    winnerLabel: String,
    participationLabel: String,
) {
    Surface(
        modifier = modifier,
        color = if (isWinner) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isWinner) 32.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = teamName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = points.toString(),
                style = TextStyle(
                    fontSize = 64.sp,
                    fontFamily = robotoFlex(
                        FontVariation.Settings(
                            FontVariation.weight(700),
                            FontVariation.width(80f),
                            FontVariation.Setting("YOPQ", 25f),
                        )
                    ),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = when {
                    isDraw -> participationLabel
                    isWinner -> winnerLabel
                    else -> participationLabel
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatsRow(
    title: String,
    value: String,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

private fun GameState.toGameOverStats(): GameOverStats {
    val allPlayedCards = rounds.flatMap { it.playedCards }
    val rankedTeams = (match?.teams ?: emptyList()).sortedByDescending { it.points }

    val correctCards = allPlayedCards.count { it.outcome == CardOutcome.CORRECT }
    val wrongCards = allPlayedCards.count { it.outcome == CardOutcome.WRONG }
    val skippedCards = allPlayedCards.count { it.outcome == CardOutcome.SKIPPED }

    val topTeam = rankedTeams.firstOrNull()
    val secondTeam = rankedTeams.getOrNull(1)
    val isDraw =
        topTeam != null && secondTeam != null && topTeam.points == secondTeam.points

    val trackedTotalSeconds = rounds.sumOf { it.durationSeconds }
    val fallbackTotalSeconds = rounds.size * (match?.settings?.roundTime ?: 0)
    val totalTimeSeconds = if (trackedTotalSeconds > 0) trackedTotalSeconds else fallbackTotalSeconds
    val secondsPerCard =
        if (allPlayedCards.isEmpty() || totalTimeSeconds <= 0) null
        else totalTimeSeconds.toDouble() / allPlayedCards.size

    return GameOverStats(
        roundsPlayed = rounds.size,
        totalCards = allPlayedCards.size,
        correctCards = correctCards,
        wrongCards = wrongCards,
        skippedCards = skippedCards,
        rankedTeams = rankedTeams,
        winnerTeamName = topTeam?.name,
        isDraw = isDraw,
        totalTimeSeconds = totalTimeSeconds,
        secondsPerCard = secondsPerCard,
    )
}

private fun formatDuration(totalSeconds: Int, text: GameOverStrings): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val seconds = safeSeconds % 60
    return text.durationFormat(minutes, seconds)
}

private fun formatPace(
    secondsPerCard: Double?,
    text: GameOverStrings,
): String {
    if (secondsPerCard == null) return text.noValue

    val roundedToOneDecimal = (secondsPerCard * 10.0).roundToInt() / 10.0
    return text.paceFormat(roundedToOneDecimal.toString())
}

@Preview
@Composable
private fun GameOverScreenPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        GameOverScreen(
            state = GameStatePreviewData.gameOver,
            onGoHome = {},
        )
    }
}