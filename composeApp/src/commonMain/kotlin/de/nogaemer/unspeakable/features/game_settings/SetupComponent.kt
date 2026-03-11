package de.nogaemer.unspeakable.features.game_settings

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.model.GameConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SetupComponent {
    val networkMode: NetworkMode
    val playerName: StateFlow<String>
    val ipAddress: StateFlow<String>
    fun updatePlayerName(name: String)
    fun updateIpAddress(ip: String)
    fun onStartGame()
}

class DefaultSetupComponent(
    componentContext: ComponentContext,
    override val networkMode: NetworkMode,
    private val onGameStarted: (GameConfig) -> Unit
) : SetupComponent, ComponentContext by componentContext {

    private val _playerName = MutableStateFlow("")
    private val _ipAddress  = MutableStateFlow("192.168.178.63")

    override val playerName = _playerName.asStateFlow()
    override val ipAddress  = _ipAddress.asStateFlow()

    override fun updatePlayerName(name: String) { _playerName.value = name }
    override fun updateIpAddress(ip: String)    { _ipAddress.value = ip }

    override fun onStartGame() {
        val config = when (networkMode) {
            NetworkMode.LOCAL -> GameConfig.Local
            NetworkMode.HOST -> GameConfig.Host(playerName.value)
            NetworkMode.CLIENT -> GameConfig.Join(ipAddress.value, playerName.value)
        }

        onGameStarted(config)
    }
}
