package de.nogaemer.unspeakable.features.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnDestroy
import de.nogaemer.unspeakable.core.model.GameConfig
import de.nogaemer.unspeakable.core.model.GameEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

interface GameComponent {
    val state: StateFlow<GameState>
    fun onEvent(event: GameEvent)
    fun drawRandomCard()
}

class DefaultGameComponent(
    componentContext: ComponentContext,
    config: GameConfig,
) : GameComponent, ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main + SupervisorJob())
    private val viewModel = GameViewModel(config = config, scope = scope)

    init {
        lifecycle.doOnDestroy {
            viewModel.closeSession()
        }
    }


    override val state: StateFlow<GameState> = viewModel.state
    override fun onEvent(event: GameEvent) = viewModel.onEvent(event)
    override fun drawRandomCard() = viewModel.drawRandomCard()
}