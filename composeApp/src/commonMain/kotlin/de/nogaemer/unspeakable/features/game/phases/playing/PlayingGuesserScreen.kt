package de.nogaemer.unspeakable.features.game.phases.ready

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexClock
import de.nogaemer.unspeakable.core.util.robotoFlexTitleVariation
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayingGuesserScreen(
    state: GameState,
) {
    val text = strings.gamePlaying

    val roundTime = state.match?.settings?.roundTime ?: 0

    var progress by remember { mutableStateOf(1f) }
    val leftTeamPoints = state.match?.teams?.getOrNull(0)?.points ?: 0
    val rightTeamPoints = state.match?.teams?.getOrNull(1)?.points ?: 0

    LaunchedEffect(state.currentRoundTime, roundTime) {
        val currentSeconds = state.currentRoundTime?.toFloat() ?: 0f
        val maxSeconds = roundTime.toFloat().coerceAtLeast(1f)

        val startProgress = currentSeconds / maxSeconds
        val endProgress = ((currentSeconds - 1).coerceAtLeast(0f)) / maxSeconds

        if (currentSeconds > 0) {
            val startTimeNanos = withFrameNanos { it }
            val oneSecondNanos = 1_000_000_000L

            while (true) {
                withFrameNanos { frameTimeNanos ->
                    val elapsedNanos = frameTimeNanos - startTimeNanos

                    if (elapsedNanos >= oneSecondNanos) {
                        progress = endProgress
                    } else {
                        val fraction = elapsedNanos.toFloat() / oneSecondNanos
                        progress = startProgress + (endProgress - startProgress) * fraction
                    }
                }
            }
        } else {
            progress = 0f
        }
    }

    Box(
        Modifier.fillMaxSize().padding(16.dp).background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 64.dp).align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                Text(
                    text = text.title,
                    style = TextStyle(
                        fontSize = 72.sp,
                        fontFamily = robotoFlex(robotoFlexTitleVariation()),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                )
                Text(
                    text = text.subtitle,
                    style = MaterialTheme.typography.titleLarge,
                )

        }

        Box(
            Modifier.fillMaxWidth().align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator(
                modifier = Modifier.size(220.dp),
                progress = { progress },
                trackStroke = Stroke(
                    width = with(LocalDensity.current) { 18.dp.toPx() },
                    cap = StrokeCap.Round,
                ),
                stroke = Stroke(
                    width = with(LocalDensity.current) { 18.dp.toPx() },
                    cap = StrokeCap.Round,
                ),
                gapSize = 14.dp,
                wavelength = 64.0.dp
            )
            Text(
                text = run {
                    val minutes = (state.currentRoundTime ?: 1) / 60
                    val seconds = (state.currentRoundTime ?: 1) % 60
                    minutes.toString().padStart(2, '0') + ":" + seconds.toString()
                        .padStart(2, '0')
                },
                style = TextStyle(
                    fontSize = 96.sp,
                    fontFamily = robotoFlexClock(),
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.15.sp,
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=375dp,height=675dp,orientation=portrait"
)
@Composable
private fun PlayingGuesserScreenPreview() {
    AppTheme {
        PlayingGuesserScreen(
            state = GameStatePreviewData.playing,
        )
    }
}