package de.nogaemer.unspeakable.features.game.phases.lobby.settings.round

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.GameRoundConfig
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.GameSettingsOverviewComponent
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData


/**
 * Connects round-count overview UI to its component state and navigation.
 */
@Composable
fun RoundCountOverviewScreen(
    component: GameSettingsOverviewComponent<GameRoundConfig>
) {
    val state by component.state.collectAsState()
    RoundCountContent(
        state = state,
        onEvent = component.onEvent,
        onNavigate = component::navigateTo,
    )
}

/**
 * Displays predefined and custom round-count selection controls.
 */
@Composable
fun RoundCountContent(
    state: GameState,
    onEvent: (GameClientEvent) -> Unit,
    onNavigate: (GameRoundConfig) -> Unit = {},
) {
    val text = strings.gameLobbySettings.gameRoundSettingsStrings

    val roundsFromState = state.match?.settings?.maxRounds ?: 10
    var roundCount by remember { mutableIntStateOf(roundsFromState) }
    var selectedRoundCountOption by remember { mutableStateOf(RoundCountOption.getByRound(roundCount)) }

    LaunchedEffect(roundsFromState) {
        roundCount = roundsFromState
        selectedRoundCountOption = RoundCountOption.getByRound(roundCount)
    }

    fun updateRoundCount(roundCountOption: RoundCountOption, value: Int) {
        selectedRoundCountOption = roundCountOption

        roundCount = value
        val settings = state.match?.settings ?: return
        onEvent(
            GameClientEvent.UpdateGameSettings(settings.copy(maxRounds = value))
        )
    }

    val isLittleSelected = selectedRoundCountOption == RoundCountOption.Little
    val isMiddleSelected = selectedRoundCountOption == RoundCountOption.Middle
    val isManySelected = selectedRoundCountOption == RoundCountOption.Many
    val isCustomSelected = selectedRoundCountOption == RoundCountOption.Custom


    Surface {
        SegmentedLazyColumn {
            addRoundCountItem(
                label = text.roundCountLittleLabel,
                supporting = text.roundCountValue(4),
                selected = isLittleSelected,
                onClick = { updateRoundCount(RoundCountOption.Little, 4) },
            )
            addRoundCountItem(
                label = text.roundCountMiddleLabel,
                supporting = text.roundCountValue(8),
                selected = isMiddleSelected,
                onClick = { updateRoundCount(RoundCountOption.Middle, 8) },
            )
            addRoundCountItem(
                label = text.roundCountManyLabel,
                supporting = text.roundCountValue(12),
                selected = isManySelected,
                onClick = { updateRoundCount(RoundCountOption.Many, 12) },
            )

            item("custom_round_count") {
                SegmentedListItem(
                    modifier = Modifier.clickable {
                        if (!isCustomSelected) updateRoundCount(RoundCountOption.Custom, roundCount)
                    },
                    selected = isCustomSelected,
                    headlineContent = { Text(text.roundCountCustomLabel) },
                    supportingContent = { Text(text.roundCountValue(roundCount)) },
                    leadingContent = {
                        RadioButton(
                            selected = isCustomSelected,
                            onClick = {
                                if (!isCustomSelected) updateRoundCount(
                                    RoundCountOption.Custom,
                                    roundCount
                                )
                            }
                        )
                    },
                    trailingContent = {
                        Row(
                            modifier = Modifier
                                .height(48.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            VerticalDivider(
                                modifier = Modifier.fillMaxHeight()
                                    .padding(vertical = 4.dp, horizontal = 16.dp),
                                thickness = 1.dp,
                                color = if (isCustomSelected) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                            )

                            Icon(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onNavigate(GameRoundConfig.CustomRoundSettings) }
                                    .padding(8.dp),
                                imageVector = Lucide.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun LazyListScope.addRoundCountItem(
    label: String,
    supporting: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    item(label) {
        SegmentedListItem(
            selected = selected,
            modifier = Modifier.clickable(onClick = onClick),
            headlineContent = { Text(label) },
            supportingContent = { Text(supporting) },
            leadingContent = {
                RadioButton(
                    selected = selected,
                    onClick = onClick
                )
            }
        )
    }
}

private enum class RoundCountOption {
    Little,
    Middle,
    Many,
    Custom;

    companion object {
        fun getByRound(rounds: Int): RoundCountOption {
            return when (rounds) {
                4 -> Little
                8 -> Middle
                12 -> Many
                else -> Custom
            }
        }
    }
}

@Preview
@Composable
fun RoundCountScreenPreview() {
    AppTheme(
        darkTheme = true,
        seedColor = Color(0xFF15DEB6),
    ) {
        RoundCountContent(
            state = GameStatePreviewData.lobby,
            onEvent = {}
        )
    }
}
