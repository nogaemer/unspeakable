package de.nogaemer.unspeakable.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nogaemer.unspeakable.model.GameEvent
import de.nogaemer.unspeakable.session.ClientGameSession
import de.nogaemer.unspeakable.session.GameSession
import de.nogaemer.unspeakable.session.HostGameSession
import de.nogaemer.unspeakable.session.LocalGameSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    //Start with a local session when app starts
    private val sessionFlow = MutableStateFlow<GameSession>(LocalGameSession())
    private val session: GameSession get() = sessionFlow.value

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<GameState> = sessionFlow
        .flatMapLatest { currentSession -> currentSession.state }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameState()
        )

    fun onEvent(event: GameEvent) {
        viewModelScope.launch {
            session.sendEvent(event)
        }
    }
    fun startHostingGame() {
        println("hosting")
        session.close()

        val hostSession = HostGameSession()
        hostSession.startServer(port = 8080)
        sessionFlow.value = hostSession
    }

    fun joinGameAsClient(ip: String) {
        session.close()

        val clientSession = ClientGameSession(ip, viewModelScope)
        viewModelScope.launch {
            clientSession.connect()
        }
        sessionFlow.value = clientSession
    }

    fun drawRandomCard(){
        onEvent(GameEvent.RequestNewRandomCard)
    }

    override fun onCleared() {
        super.onCleared()
        session.close()
    }
}
