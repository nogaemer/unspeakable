package de.nogaemer.unspeakable.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.lyricist.strings
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import de.nogaemer.unspeakable.core.components.menu.NestedMenuScreen
import de.nogaemer.unspeakable.core.model.GamePhase
import de.nogaemer.unspeakable.features.game.phases.connection.ConnectionLostScreen
import de.nogaemer.unspeakable.features.game.phases.gameover.GameOverScreen
import de.nogaemer.unspeakable.features.game.phases.lobby.LobbyScreen
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.GameSettingsOverviewScreen
import de.nogaemer.unspeakable.features.game.phases.overview.RoundOverviewScreen
import de.nogaemer.unspeakable.features.game.phases.playing.PlayingScreen
import de.nogaemer.unspeakable.features.game.phases.ready.GameReadyScreen

/**
 * Routes to the active game phase screen and nested lobby/settings content.
 */
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun GameScreen(component: DefaultGameComponent, onBack: () -> Unit, onGoHome: () -> Unit) {
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

                is GameComponent.GameChild.GameSettingsChild -> NestedMenuScreen(
                    component = instance.component,
                    rootTitle = strings.gameLobbySettings.lobbySettingsTitle,
                    showBackOnOverview = true,
                    onRootBack = component::goBack,
                    overviewContent = { GameSettingsOverviewScreen(it) }
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
        )

        GamePhase.ROUND_SUMMARY -> RoundOverviewScreen(
            state = state,
            onEvent = component::onEvent,
        )

        GamePhase.GAME_OVER -> GameOverScreen(
            state,
            onGoHome,
        )

        GamePhase.CONNECTION_LOST -> ConnectionLostScreen(
            onGoHome = onGoHome,
        )
    }
}
