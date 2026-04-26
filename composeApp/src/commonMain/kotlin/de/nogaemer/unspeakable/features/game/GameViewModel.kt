package de.nogaemer.unspeakable.features.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.GameConfig
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.features.game.session.ClientSession
import de.nogaemer.unspeakable.features.game.session.GameSession
import de.nogaemer.unspeakable.features.game.session.HostSession
import de.nogaemer.unspeakable.features.game.session.LocalSession
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Creates the appropriate game session and exposes its state to UI.
 */
class GameViewModel(config: GameConfig) : ViewModel() {

    private var isSessionClosed = false

    private val session: GameSession = when (config) {
        is GameConfig.Local -> LocalSession(
            playerName = config.playerName,
            profilePicture = config.profilePicture,
            scope = viewModelScope,
            cardDao = Graph.dao,
            lang = Graph.settings.appSettings.locales.lang,
        )

        is GameConfig.Host -> HostSession(
            playerName = config.playerName,
            profilePicture = config.profilePicture,
            scope = viewModelScope,
            cardDao = Graph.dao,
            lang = Graph.settings.appSettings.locales.lang,
        )

        is GameConfig.Join -> ClientSession(
            hostIp = config.hostIp,
            playerName = config.playerName,
            profilePicture = config.profilePicture,
            scope = viewModelScope,
        )
    }

    val state: StateFlow<GameState> = session.state

    init {
        viewModelScope.launch { session.start() }
    }

    fun onEvent(event: GameClientEvent) {
        viewModelScope.launch { session.sendEvent(event) }
    }

    fun onEventAs(event: GameClientEvent, playerId: String) {
        viewModelScope.launch { session.sendEventAs(event, playerId) }
    }

    fun closeSession() {
        if (isSessionClosed) return
        isSessionClosed = true
        session.close()
    }

    override fun onCleared() {
        super.onCleared()
        closeSession()
    }
}
