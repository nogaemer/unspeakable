package de.nogaemer.unspeakable.features.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnDestroy
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

interface GameComponent {
    val state: StateFlow<GameState>
    fun onEvent(event: GameClientEvent)
    fun drawRandomCard()
}

class DefaultGameComponent(
    componentContext: ComponentContext,
    config: GameConfig,
) : GameComponent, ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main + SupervisorJob())
    private val viewModel = GameViewModel(config = config)

    init {
        lifecycle.doOnDestroy {
            viewModel.closeSession()
        }
    }


    override val state: StateFlow<GameState> = viewModel.state
    override fun onEvent(event: GameClientEvent) = viewModel.onEvent(event)
    override fun drawRandomCard() = viewModel.drawRandomCard()
}