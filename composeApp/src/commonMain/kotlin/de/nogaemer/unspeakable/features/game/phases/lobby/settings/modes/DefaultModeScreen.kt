package de.nogaemer.unspeakable.features.game.phases.lobby.settings.modes

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.mode.GameMode
import de.nogaemer.unspeakable.core.mode.ModeRegistry
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData

/**
 * Renders custom round-count controls for lobby settings.
 */
@Composable
fun DefaultModeScreen(
    component: DefaultModeComponent,
) {
    val state by component.state.collectAsState()
    DefaultModeScreen(
        state = state,
        onEvent = component.onEvent,
    )
}

@Composable
private fun DefaultModeScreen(
    state: GameState,
    onEvent: (GameClientEvent) -> Unit,
) {
    val text = strings.gameLobbySettings.gameRoundSettingsStrings
    val availableModes: List<GameMode> = ModeRegistry.allModes
    val settings = state.match?.settings ?: return
    val selectedModes = settings.enabledModeIds

    fun updateSelectedMode(mode: GameMode, newValue: Boolean) {
        onEvent(
            GameClientEvent.UpdateGameSettings(
                settings.copy(
                    enabledModeIds = if (newValue) {
                        selectedModes + mode.id
                    } else {
                        selectedModes - mode.id
                    }
                )
            )
        )
    }

    SegmentedColumn {
        availableModes.forEach { mode ->
            SegmentedListItem(
                selected = settings.enabledModeIds.contains(mode.id),
                modifier = Modifier.clickable(
                    onClick = {
                        updateSelectedMode(mode, !selectedModes.contains(mode.id))
                    }
                ),
                headlineContent = {
                    Text(mode.displayName(strings))
                },
                supportingContent = {
                    Text(mode.description(strings))
                },
                trailingContent = {
                    Checkbox(
                        checked = settings.enabledModeIds.contains(mode.id),
                        onCheckedChange = {
                            updateSelectedMode(mode, it)
                        }
                    )
                },
            )
        }
    }
}

/**
 * Screen for selecting and configuring game modes in the lobby.
 */
@Preview()
@Composable
fun DefaultModeScreenPreview() {
    DefaultModeScreen(
        state = GameStatePreviewData.lobby,
        onEvent = {},
    )
}
