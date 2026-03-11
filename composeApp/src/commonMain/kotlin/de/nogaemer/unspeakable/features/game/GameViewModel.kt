package de.nogaemer.unspeakable.features.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nogaemer.unspeakable.core.model.GameConfig
import de.nogaemer.unspeakable.core.model.GameEvent
import de.nogaemer.unspeakable.features.game.session.ClientGameSession
import de.nogaemer.unspeakable.features.game.session.GameSession
import de.nogaemer.unspeakable.features.game.session.HostGameSession
import de.nogaemer.unspeakable.features.game.session.LocalGameSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(config: GameConfig, scope: CoroutineScope) : ViewModel() {

    private val initialSession: GameSession = when (config) {
        is GameConfig.Local -> LocalGameSession(scope)
        is GameConfig.Host -> HostGameSession(config.playerName, scope)
        is GameConfig.Join -> ClientGameSession(config.hostIp, config.playerName, scope)
    }

    private val sessionFlow = MutableStateFlow(initialSession)
    private val session: GameSession get() = sessionFlow.value

    init {
        when (config) {
            is GameConfig.Host -> viewModelScope.launch(Dispatchers.Default) {
                (initialSession as HostGameSession).startServer(port = 8080)
            }

            is GameConfig.Join -> viewModelScope.launch {
                (initialSession as ClientGameSession).connect()
            }
            GameConfig.Local -> Unit
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<GameState> = sessionFlow
        .flatMapLatest { currentSession -> currentSession.state }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = sessionFlow.value.state.value
        )

    fun onEvent(event: GameEvent) {
        viewModelScope.launch {
            session.sendEvent(event)
        }
    }

    fun drawRandomCard(){
        onEvent(GameEvent.RequestNewRandomCard)
    }

    fun closeSession() {
        session.close()
    }


    public override fun onCleared() {
        super.onCleared()
        closeSession()
    }
}