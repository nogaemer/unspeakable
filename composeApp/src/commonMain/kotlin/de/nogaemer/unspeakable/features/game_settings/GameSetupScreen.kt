package de.nogaemer.unspeakable.features.game_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupScreen(
    component: SetupComponent
) {
    val playerName by component.playerName.collectAsState()
    val ipAddress by component.ipAddress.collectAsState()
    val networkMode = component.networkMode
    val text = strings.gameSetup

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        if (networkMode == NetworkMode.CLIENT) {
            TextField(
                value = ipAddress,
                onValueChange = component::updateIpAddress,
                label = { Text(text.ipAddress) })
        }
        if (networkMode != NetworkMode.LOCAL) {
            TextField(
                value = playerName,
                onValueChange = component::updatePlayerName,
                label = { Text(text.playerName) })
        }


        Button(
            onClick = { component.onStartGame() },
        ) {
            Text(text.startGame)
        }
    }

}

enum class NetworkMode {
    LOCAL,
    CLIENT,
    HOST
}