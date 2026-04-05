package de.nogaemer.unspeakable.features.game_setup

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.model.GameConfig
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.core.model.ProfileShape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SetupComponent {
    val networkMode: NetworkMode
    val playerName: StateFlow<String>
    val playerProfilePicture: StateFlow<ProfilePicture>
    val ipAddress: StateFlow<String>
    fun updatePlayerName(name: String)
    fun updatePlayerProfilePicture(picture: ProfilePicture)
    fun updateIpAddress(ip: String)
    fun onStartGame()
}

class DefaultSetupComponent(
    componentContext: ComponentContext,
    override val networkMode: NetworkMode,
    private val onGameStarted: (GameConfig) -> Unit
) : SetupComponent, ComponentContext by componentContext {

    private val _playerName = MutableStateFlow("")
    private val _playerProfilePicture = MutableStateFlow(
        ProfilePicture(
            shape = ProfileShape.CIRCLE,
            image = ""
        )
    )
    private val _ipAddress = MutableStateFlow("")

    override val playerName = _playerName.asStateFlow()
    override val playerProfilePicture = _playerProfilePicture.asStateFlow()
    override val ipAddress = _ipAddress.asStateFlow()

    override fun updatePlayerName(name: String) {
        _playerName.value = name
    }

    override fun updatePlayerProfilePicture(picture: ProfilePicture) {
        _playerProfilePicture.value = picture
    }

    override fun updateIpAddress(ip: String) {
        _ipAddress.value = ip
    }

    override fun onStartGame() {

        val config: GameConfig = when (networkMode) {
            NetworkMode.LOCAL -> GameConfig.Local(
                playerName = playerName.value,
                profilePicture = playerProfilePicture.value
            )

            NetworkMode.HOST -> GameConfig.Host(
                playerName = playerName.value,
                profilePicture = playerProfilePicture.value
            )

            NetworkMode.CLIENT -> GameConfig.Join(
                playerName = playerName.value,
                profilePicture = playerProfilePicture.value,
                hostIp = ipAddress.value
            )
        }

        onGameStarted(config)
    }
}
