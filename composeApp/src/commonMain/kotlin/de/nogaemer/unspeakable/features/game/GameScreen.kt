package de.nogaemer.unspeakable.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.features.game.phases.lobby.LobbyScreen
import de.nogaemer.unspeakable.features.game.phases.lobby.LobbySettingsScreen
import de.nogaemer.unspeakable.features.game.phases.overview.RoundOverviewScreen
import de.nogaemer.unspeakable.features.game.phases.playing.PlayingScreen
import de.nogaemer.unspeakable.features.game.phases.ready.GameReadyScreen

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun GameScreen(component: DefaultGameComponent, onBack: () -> Unit) {
    val state by component.state.collectAsState()

    when (state.phase) {
        GamePhase.SETUP -> Children(
            stack = component.stack,
            animation = predictiveBackAnimation(
                backHandler = component.backHandler,
                onBack = component::goBack,
                selector = { backEvent, _, _ -> androidPredictiveBackAnimatableV2(backEvent) },
                fallbackAnimation = stackAnimation(slide() + fade()),
            )
        ) { child ->
            when (val instance = child.instance) {
                is GameComponent.GameChild.LobbyChild -> LobbyScreen(
                    state = state,
                    onEvent = component::onEvent,
                    onOpenSettings = component::navigateToLobbySettings,
                    onBack = onBack,
                )

                is GameComponent.GameChild.LobbySettingsChild -> LobbySettingsScreen(
                    component = instance.component,
                )
            }
        }


        GamePhase.READY -> GameReadyScreen(
            state = state,
            onEvent = component::onEvent,
        )

        GamePhase.PLAYING -> PlayingScreen(
            state = state,
            onEvent = component::onEvent,
            drawRandomCard = component::drawRandomCard
        )

        GamePhase.ROUND_SUMMARY -> RoundOverviewScreen(
            state = state,
            onEvent = component::onEvent,
        )

        GamePhase.GAME_OVER -> TODO()
    }
}
