package de.nogaemer.unspeakable.features.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SkipForward
import com.composables.icons.lucide.X
import de.nogaemer.unspeakable.core.model.GameEvent
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexCardItems
import de.nogaemer.unspeakable.core.util.robotoFlexClock

@Composable
fun GameScreen(component: GameComponent) {
    val state by component.state.collectAsState()
    PlayingScreen(
        state = state,
        onEvent = component::onEvent,
        drawRandomCard = component::drawRandomCard
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayingScreen(
    state: GameState,
    onEvent: (event: GameEvent) -> Unit,
    drawRandomCard: () -> Unit
) {


    var progress by remember { mutableStateOf(1f) }

    // Re-run this block if the round time changes
    LaunchedEffect(state.currentRoundTime, state.maxRoundTime) {
        val currentSeconds = state.currentRoundTime?.toFloat() ?: 0f
        val maxSeconds = state.maxRoundTime?.toFloat()?.coerceAtLeast(1f) ?: 1f

        // Where the circle should be RIGHT NOW
        val startProgress = currentSeconds / maxSeconds
        // Where the circle should be ONE SECOND FROM NOW
        val endProgress = ((currentSeconds - 1).coerceAtLeast(0f)) / maxSeconds

        // If we have time left to animate...
        if (currentSeconds > 0) {
            val startTimeNanos = withFrameNanos { it }
            val oneSecondNanos = 1_000_000_000L // 1 second in nanoseconds

            // Loop forever until the coroutine is cancelled (which happens
            // when the server sends the next 1-second tick!)
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    val elapsedNanos = frameTimeNanos - startTimeNanos

                    if (elapsedNanos >= oneSecondNanos) {
                        progress = endProgress
                    } else {
                        // Calculate exact percentage between the start and end of this second
                        val fraction = elapsedNanos.toFloat() / oneSecondNanos
                        // Linear interpolation
                        progress = startProgress + (endProgress - startProgress) * fraction
                    }
                }
            }
        } else {
            progress = 0f
        }
    }



    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                //Left Team indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 8.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 8.dp
                                )
                            )
                            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
                    ) {
                        Text(
                            "Team A",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier.size(40.dp).clip(MaterialShapes.Pentagon.toShape())
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "12",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                //Right Team indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(MaterialShapes.Pentagon.toShape())
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "12",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(
                                    topStart = 8.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 8.dp,
                                    bottomEnd = 16.dp
                                )
                            )
                            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
                    ) {
                        Text(
                            "Team B",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(148.dp),
                    progress = { progress },
                    trackStroke = Stroke(
                        width =
                            with(LocalDensity.current) {
                                14.dp.toPx()
                            },
                        cap = StrokeCap.Round,
                    ),
                    stroke = Stroke(
                        width =
                            with(LocalDensity.current) {
                                14.dp.toPx()
                            },
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
                        fontSize = 64.sp,
                        fontFamily = robotoFlexClock(),
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.15.sp,
                    )
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = 48.dp,
                        topEnd = 48.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .padding(start = 10.dp, top = 20.dp, end = 10.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.currentCard != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
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
                        )
                    )

                    val forbiddenWords = listOf(
                        state.currentCard.forbidden1,
                        state.currentCard.forbidden2,
                        state.currentCard.forbidden3,
                        state.currentCard.forbidden4,
                        state.currentCard.forbidden5
                    )

                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                    ) {
                        for (word in forbiddenWords) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RoundedCornerShape(size = 4.dp)
                                    ).padding(horizontal = 12.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(
                                    0.dp,
                                    Alignment.CenterVertically
                                ),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Text(
                                    text = word.uppercase(),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontFamily = robotoFlexCardItems(),
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalIconButton(
                    onClick = { TODO() },
                    modifier = Modifier.size(96.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    content = {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = null
                        )
                    }
                )
                FilledTonalIconButton(
                    onClick = { TODO() },
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(28.dp),
                    content = {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = null
                        )
                    }
                )
                FilledTonalIconButton(
                    onClick = drawRandomCard,
                    modifier = Modifier.size(96.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    content = {
                        Icon(
                            imageVector = Lucide.SkipForward,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}


// Font family helpers moved to FontFamilies.kt
