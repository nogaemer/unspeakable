package de.nogaemer.unspeakable.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.features.game.phases.LobbyScreen
import de.nogaemer.unspeakable.features.game.phases.PlayingScreen

@Composable
fun GameScreen(component: GameComponent) {
    val state by component.state.collectAsState()

    when (state.phase) {
        GamePhase.SETUP -> LobbyScreen(
            state,
            onEvent = component::onEvent,
        )

        GamePhase.READY -> TODO()

        GamePhase.PLAYING -> PlayingScreen(
            state = state,
            onEvent = component::onEvent,
            drawRandomCard = component::drawRandomCard
        )

        GamePhase.ROUND_SUMMARY -> TODO()

        GamePhase.GAME_OVER -> TODO()
    }
}
