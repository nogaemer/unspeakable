package de.nogaemer.unspeakable.features.game.phases.lobby

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.settings.DefaultTopAppBar
import kotlin.math.roundToInt

@Composable
fun LobbySettingsScreen(component: LobbySettingsComponent) {
    val state by component.state.collectAsState()
    val text = strings.gameLobbySettings

    DefaultTopAppBar(
        title = text.lobbySettingsTitle,
        onBack = component::goBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SegmentedColumn(
                segmentTitle = text.roundsSettings
            ) {
                val timeAnchors = listOf(0, 30, 60, 120, 300, 600, 1800)
                var roundTime by remember { mutableIntStateOf(state.match?.settings?.roundTime ?: 30) }
                SegmentedListItem(
                    modifier = Modifier.clickable(
                        onClick = {

                        }
                    ),
                    headlineContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text.roundTime)
                            Text(
                                text = formatDuration(roundTime),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    supportingContent = {
                        AnchoredSlider(
                            anchors = timeAnchors,
                            selectedValue = roundTime,
                            onValueSelected = {
                                component.onEvent(
                                    GameClientEvent.UpdateGameSettings(
                                        state.match?.settings?.copy(
                                            roundTime = it
                                        ) ?: return@AnchoredSlider
                                    )
                                )
                            },
                            onValueChange = { roundTime = it },
                        )
                    }
                )

                SegmentedListItem(
                    modifier = Modifier.clickable(
                        onClick = {
                        }
                    ),
                    headlineContent = {
                        Text(text.roundsPerTeam)
                    },
                    supportingContent = {
                        Text(text.roundsPerTeamDescription)
                    }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnchoredSlider(
    anchors: List<Int>,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
    onValueChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var isDragging by remember { mutableStateOf(false) }

    var liveValue by remember { mutableIntStateOf(selectedValue) }

    val state = rememberSliderState(
        value = selectedValue.toSliderPosition(anchors),
        steps = 0,
        valueRange = 0f..1f,
        onValueChangeFinished = {
            isDragging = false
            onValueSelected(liveValue)
        }
    )


    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start, is PressInteraction.Press -> isDragging = true
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.value }
            .collect { sliderValue ->
                val anchored = sliderValue.toAnchoredValue(anchors)
                liveValue = anchored
                onValueChange(anchored)
            }
    }

    // only sync from server when not dragging - need this if ill later show the settings to the clients.
    LaunchedEffect(selectedValue) {
        if (!isDragging) {
            state.value = selectedValue.toSliderPosition(anchors)
        }
    }

    Box(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Slider(
            state = state,
            interactionSource = interactionSource,
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    trackCornerSize = 8.dp,
                    Modifier.height(24.dp),
                    drawStopIndicator = null,
                )
            }
        )
    }
}



fun formatDuration(seconds: Int): String = when {
    seconds < 60 -> "${seconds}s"
    seconds % 60 == 0 -> "${seconds / 60}m"
    else -> "${seconds / 60}m ${seconds % 60}s"
}

fun Float.toAnchoredValue(anchors: List<Int>): Int {
    val scaled = this * (anchors.size - 1)
    val lo = scaled.toInt().coerceIn(0, anchors.size - 2)
    val hi = lo + 1
    val fraction = scaled - lo
    val rawValue = lerp(anchors[lo].toFloat(), anchors[hi].toFloat(), fraction).roundToInt()

    return when {
        rawValue < 30 -> rawValue
        rawValue < 60 -> rawValue / 2 * 2
        rawValue < 120 -> rawValue / 5 * 5
        rawValue < 300 -> rawValue / 10 * 10
        else -> rawValue / 60 * 60
    }
}

fun Int.toSliderPosition(anchors: List<Int>): Float {
    // Find which segment this value falls in
    val lo = anchors.indexOfLast { it <= this }.coerceAtLeast(0)
    val hi = (lo + 1).coerceAtMost(anchors.lastIndex)
    if (lo == hi) return lo.toFloat() / (anchors.size - 1)

    val fraction = (this - anchors[lo]).toFloat() / (anchors[hi] - anchors[lo])
    return (lo + fraction) / (anchors.size - 1)
}
