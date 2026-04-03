package de.nogaemer.unspeakable.features.game.phases.playing

import androidx.compose.runtime.Composable
import de.nogaemer.unspeakable.core.components.LoadingScreen
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameRole
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.phases.ready.PlayingGuesserScreen

@Composable
fun PlayingScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
) {
    when (state.role) {
        GameRole.GUESSER -> PlayingGuesserScreen(state)
        GameRole.EXPLAINER -> PlayingExplainerScreen(state, onEvent)
        GameRole.OPPONENT -> PlayingOpponentScreen(state, onEvent)
        null -> LoadingScreen()
    }
}