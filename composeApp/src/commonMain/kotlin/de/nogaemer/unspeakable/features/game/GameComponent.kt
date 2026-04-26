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
import de.nogaemer.unspeakable.features.game.GameComponent.GameChild.GameSettingsChild
import de.nogaemer.unspeakable.features.game.GameComponent.GameChild.LobbyChild
import de.nogaemer.unspeakable.features.game.phases.lobby.settings.DefaultGameSettingsComponent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

/**
 * Defines root destinations for the in-game lobby navigation stack.
 */
@Serializable
sealed class LobbyConfig {
    @Serializable
    data object Lobby : LobbyConfig()

    @Serializable
    data object GameSettings : LobbyConfig()
}


/**
 * Exposes game state and actions used by the game feature UI.
 */
interface GameComponent {
    val state: StateFlow<GameState>
    val stack: Value<ChildStack<LobbyConfig, GameChild>>
    fun onEvent(event: GameClientEvent)
    fun onEventAs(event: GameClientEvent, playerId: String)
    fun goBack()

    /**
     * Represents children hosted inside the lobby stack.
     */
    sealed class GameChild {
        data class LobbyChild(val component: GameComponent) : GameChild()
        data class GameSettingsChild(val component: DefaultGameSettingsComponent) : GameChild()
    }
}


/**
 * Hosts game session state and lobby-stack navigation for the game flow.
 */
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
            LobbyConfig.Lobby -> LobbyChild(this)
            LobbyConfig.GameSettings -> GameSettingsChild(
                DefaultGameSettingsComponent(
                    ctx = ctx,
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    goBack = { navigation.pop() },
                )
            )
        }

    override fun onEvent(event: GameClientEvent) = viewModel.onEvent(event)
    override fun onEventAs(event: GameClientEvent, playerId: String) =
        viewModel.onEventAs(event, playerId)

    override fun goBack() = navigation.pop()

    fun closeSession() = viewModel.closeSession()

    @OptIn(DelicateDecomposeApi::class)
    fun navigateToLobbySettings() = navigation.push(LobbyConfig.GameSettings)

    override val state = viewModel.state
}
