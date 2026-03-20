package de.nogaemer.unspeakable.features.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameConfig
import de.nogaemer.unspeakable.features.game.phases.lobby.DefaultLobbySettingsComponent
import de.nogaemer.unspeakable.features.game.phases.lobby.LobbySettingsComponent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
sealed class LobbyConfig {
    @Serializable
    data object Lobby : LobbyConfig()

    @Serializable
    data object LobbySettings : LobbyConfig()
}


interface GameComponent {
    val state: StateFlow<GameState>
    val stack: Value<ChildStack<LobbyConfig, GameChild>>
    fun onEvent(event: GameClientEvent)
    fun drawRandomCard()
    fun goBack()

    sealed class GameChild {
        data class LobbyChild(val component: GameComponent) : GameChild()
        data class LobbySettingsChild(val component: LobbySettingsComponent) : GameChild()
    }
}


class DefaultGameComponent(
    componentContext: ComponentContext,
    config: GameConfig,
) : GameComponent, ComponentContext by componentContext {

    private val viewModel = GameViewModel(config = config)
    private val navigation = StackNavigation<LobbyConfig>()

    override val stack = childStack(
        source = navigation,
        serializer = LobbyConfig.serializer(),
        initialConfiguration = LobbyConfig.Lobby,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: LobbyConfig, ctx: ComponentContext): GameComponent.GameChild =
        when (config) {
            LobbyConfig.Lobby -> GameComponent.GameChild.LobbyChild(this)
            LobbyConfig.LobbySettings -> GameComponent.GameChild.LobbySettingsChild(
                DefaultLobbySettingsComponent(
                    ctx = ctx,
                    state = viewModel.state,
                    _onEvent = viewModel::onEvent,
                    _goBack = { navigation.pop() },
                )
            )
        }

    override fun onEvent(event: GameClientEvent) = viewModel.onEvent(event)
    override fun drawRandomCard() = viewModel.drawRandomCard()
    override fun goBack() = navigation.pop()

    fun closeSession() = viewModel.closeSession()

    @OptIn(DelicateDecomposeApi::class)
    fun navigateToLobbySettings() = navigation.push(LobbyConfig.LobbySettings)

    override val state = viewModel.state
}
