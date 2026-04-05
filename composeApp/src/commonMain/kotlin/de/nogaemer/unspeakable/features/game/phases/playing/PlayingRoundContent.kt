package de.nogaemer.unspeakable.features.game.phases.playing

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.TeamPoints
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.i18n.SabotageGameModeStrings
import de.nogaemer.unspeakable.core.mode.modes.SabotageMode
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexClock
import de.nogaemer.unspeakable.features.game.GameState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun PlayingRoundContent(
    state: GameState,
    forbiddenTrailingContent: (@Composable (word: String) -> Unit)? = null,
    actions: @Composable () -> Unit,
) {
    val text = strings.game
    val sabotageText = strings.game.gameModesStrings[SabotageMode.id]!! as SabotageGameModeStrings
    val leftTeamPoints = state.match?.teams?.getOrNull(0)?.points ?: 0
    val rightTeamPoints = state.match?.teams?.getOrNull(1)?.points ?: 0
    val roundTime = state.match?.settings?.roundTime ?: 0
    val progress = rememberRoundProgress(state.currentRoundTime, roundTime)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.lastSabotage) {
        val sabotage = state.lastSabotage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = sabotageText.toastMessage(sabotage.byPlayer.name, sabotage.newTabooWord),
            duration = SnackbarDuration.Short,
            withDismissAction = true,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Box(
                Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 12.dp),
            ) {
                TeamPoints(
                    teamAName = text.teamA,
                    teamBName = text.teamB,
                    leftTeamPoints = leftTeamPoints,
                    rightTeamPoints = rightTeamPoints,
                )
            }
            TwoPartLayout(
                modifier = Modifier.fillMaxSize(),
                top = {
                    Column(
                        Modifier.fillMaxSize()
                            .padding(horizontal = 10.dp).padding(bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            24.dp,
                            Alignment.CenterVertically,
                        ),
                    ) {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularWavyProgressIndicator(
                                modifier = Modifier.size(148.dp),
                                progress = { progress },
                                trackStroke = Stroke(
                                    width = with(LocalDensity.current) { 14.dp.toPx() },
                                    cap = StrokeCap.Round,
                                ),
                                stroke = Stroke(
                                    width = with(LocalDensity.current) { 14.dp.toPx() },
                                    cap = StrokeCap.Round,
                                ),
                                gapSize = 14.dp,
                                wavelength = 64.0.dp,
                            )
                            Text(
                                text = run {
                                    val minutes = (state.currentRoundTime ?: 1) / 60
                                    val seconds = (state.currentRoundTime ?: 1) % 60
                                    minutes.toString().padStart(2, '0') + ":" + seconds.toString()
                                        .padStart(2, '0')
                                },
                                style = TextStyle(
                                    fontSize = 64.sp,
                                    fontFamily = robotoFlexClock(),
                                    fontWeight = FontWeight(500),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 0.15.sp,
                                ),
                            )
                        }
                    }
                },
                bottom = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(
                                    topStart = 48.dp,
                                    topEnd = 48.dp,
                                ),
                            )
                            .padding(start = 10.dp, top = 20.dp, end = 10.dp, bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (state.currentCard != null) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f, fill = false)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = state.currentCard.word.uppercase(),
                                    style = TextStyle(
                                        fontSize = 72.sp,
                                        fontFamily = robotoFlex(),
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textAlign = TextAlign.Center,
                                    ),
                                )

                                val forbiddenWords = state.currentCard.forbiddenWords

                                SegmentedColumn(
                                    Modifier.fillMaxWidth()
                                        .weight(1f, fill = false)
                                        .clip(RoundedCornerShape(16.dp))
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                                    contentPadding = PaddingValues(0.dp),
                                ) {
                                    for (word in forbiddenWords) {
                                        val violatedWord = state.violatedForbiddenWord
                                        val isViolated = violatedWord.equals(word, ignoreCase = true)
                                        val animationDuration = state.violatedForbiddenWordDurationMs
                                            .coerceAtLeast(1L).toInt()


                                        val baseTextColor = MaterialTheme.colorScheme.onBackground
                                        val targetTextColor = if (isViolated) MaterialTheme.colorScheme.onErrorContainer else baseTextColor

                                        val baseBackgroundColor = MaterialTheme.colorScheme.surfaceContainer
                                        val targetBackgroundColor = if (isViolated) MaterialTheme.colorScheme.errorContainer else baseBackgroundColor

                                        val animatedTextColor by animateColorAsState(
                                            targetValue = targetTextColor,
                                            animationSpec = tween(
                                                durationMillis = animationDuration,
                                                easing = FastOutSlowInEasing
                                            ),
                                            label = "forbidden-word-color",
                                        )
                                        val animatedBackgroundColor by animateColorAsState(
                                            targetValue = targetBackgroundColor,
                                            animationSpec = tween(
                                                durationMillis = animationDuration,
                                                easing = FastOutSlowInEasing
                                            ),
                                            label = "forbidden-word-color",
                                        )

                                        SegmentedListItem(
                                            headlineContent = {
                                                Text(
                                                    text = word.uppercase(),
                                                    style = TextStyle(
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                    ),
                                                )
                                            },
                                            trailingContent = if (forbiddenTrailingContent != null) {
                                                { forbiddenTrailingContent(word) }
                                            } else {
                                                null
                                            },
                                            colors = ListItemDefaults.colors(
                                                containerColor = animatedBackgroundColor,
                                                headlineColor = animatedTextColor,
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        actions()
                    }
                },
            )
        }
    }
}

@Composable
private fun rememberRoundProgress(currentRoundTime: Int?, roundTime: Int): Float {
    var progress by remember { mutableStateOf(1f) }

    LaunchedEffect(currentRoundTime, roundTime) {
        val currentSeconds = currentRoundTime?.toFloat() ?: 0f
        val maxSeconds = roundTime.toFloat().coerceAtLeast(1f)

        val startProgress = currentSeconds / maxSeconds
        val endProgress = ((currentSeconds - 1).coerceAtLeast(0f)) / maxSeconds

        if (currentSeconds > 0) {
            val startTimeNanos = withFrameNanos { it }
            val oneSecondNanos = 1_000_000_000L
            var finished = false

            while (!finished) {
                withFrameNanos { frameTimeNanos ->
                    val elapsedNanos = frameTimeNanos - startTimeNanos

                    if (elapsedNanos >= oneSecondNanos) {
                        progress = endProgress
                        finished = true
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

    return progress
}




