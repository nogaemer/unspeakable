package de.nogaemer.unspeakable.features.game_setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.core.model.ProfileShape
import de.nogaemer.unspeakable.core.util.ImageUtils
import de.nogaemer.unspeakable.core.util.codeToIp

private enum class SetupStep { LOBBY_CODE, ENTER_NAME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    component: SetupComponent,
    onBack: () -> Unit,
) {
    val playerName by component.playerName.collectAsState()
    val networkMode = component.networkMode

    var lobbyCode by rememberSaveable { mutableStateOf("") }
    var step by rememberSaveable {
        mutableStateOf(
            if (networkMode == NetworkMode.CLIENT) SetupStep.LOBBY_CODE
            else SetupStep.ENTER_NAME
        )
    }

    val profilePicture = ImageUtils.createProfilePicture()

    val text = strings
    val colorScheme = MaterialTheme.colorScheme


    fun goBack() {
        if (step == SetupStep.ENTER_NAME) {
            step = SetupStep.LOBBY_CODE
        } else {
            onBack()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().imePadding()
            .background(colorScheme.background)
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
    ) {
        IconButton(
            onClick = { goBack() },
            modifier = Modifier
                .padding(12.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(colorScheme.surfaceVariant.copy(alpha = 0.2f)).zIndex(1f),
        ) {
            Icon(
                imageVector = Lucide.ArrowLeft,
                contentDescription = strings.common.back,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

        }
        when {
            networkMode == NetworkMode.CLIENT && step == SetupStep.LOBBY_CODE -> {
                JoinLobbyScreen(
                    lobbyCode = lobbyCode,
                    onCodeChange = { lobbyCode = it },
                    onJoin = {
                        val ip = try {
                            codeToIp(lobbyCode)
                        } catch (_: Exception) {
                            lobbyCode
                        }
                        component.updateIpAddress(ip)
                        step = SetupStep.ENTER_NAME
                    }
                )
            }

            else -> {
                EnterNameScreen(
                    playerName = playerName,
                    onNameChange = component::updatePlayerName,
                    onConfirm = {
                        component.updatePlayerProfilePicture(
                            ProfilePicture(
                                ProfileShape.entries.random(),
                                ImageUtils.imageToBase64(profilePicture)
                            )
                        )
                        component.onStartGame()
                    }
                )
            }
        }
    }
}


enum class NetworkMode {
    LOCAL,
    CLIENT,
    HOST
}
