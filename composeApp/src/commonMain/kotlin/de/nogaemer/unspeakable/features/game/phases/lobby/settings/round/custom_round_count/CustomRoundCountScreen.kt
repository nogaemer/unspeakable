package de.nogaemer.unspeakable.features.game.phases.lobby.settings.round.custom_round_count

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.AnchoredSlider
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData
import kotlin.math.ceil

/**
 * Renders custom round-count controls for lobby settings.
 */
@Composable
fun CustomRoundCountScreen(
    component: DefaultCustomRoundCountComponent,
) {
    val state by component.state.collectAsState()
    CustomRoundCountScreen(
        state = state,
        onEvent = component.onEvent,
    )
}

@Composable
private fun CustomRoundCountScreen(
    state: GameState,
    onEvent: (GameClientEvent) -> Unit,
) {
    val text = strings.gameLobbySettings.gameRoundSettingsStrings

    val roundsFromState = state.match?.settings?.maxRounds ?: 10
    var selectedRoundCount by remember { mutableIntStateOf(roundsFromState) }

    fun updateRoundCount(value: Int) {
        selectedRoundCount = value
        val settings = state.match?.settings ?: return
        onEvent(
            GameClientEvent.UpdateGameSettings(settings.copy(maxRounds = value)),
        )
    }

    SegmentedColumn {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            SegmentedListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text.customRoundsSliderLabel)
                        Text(
                            text = text.roundCountValue(ceil(selectedRoundCount / 2.0).toInt()),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                },
                supportingContent = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        AnchoredSlider(
                            anchors = (1..10).toList(),
                            selectedValue = ceil(selectedRoundCount / 2.0).toInt(),
                            onValueSelected = { updateRoundCount(it * 2) },
                            onValueChange = { selectedRoundCount = it * 2 },
                        )
                    }
                }
            )
        }
    }
}

/**
 * Screen for customizing the round count in the lobby settings.
 */
@Preview()
@Composable
fun CustomRoundCountScreenPreview() {
    CustomRoundCountScreen(
        state = GameStatePreviewData.lobby,
        onEvent = {},
    )
}
