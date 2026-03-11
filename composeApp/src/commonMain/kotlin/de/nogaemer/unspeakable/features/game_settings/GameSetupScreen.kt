package de.nogaemer.unspeakable.features.game_settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupScreen(
    component: SetupComponent
) {
    val playerName by component.playerName.collectAsState()
    val ipAddress by component.ipAddress.collectAsState()
    val networkMode = component.networkMode

    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        if (networkMode == NetworkMode.CLIENT) {
            TextField(
                value = ipAddress,
                onValueChange = component::updateIpAddress,
                label = { Text("IP Address") })
        }
        if (networkMode != NetworkMode.LOCAL) {
            TextField(
                value = playerName,
                onValueChange = component::updatePlayerName,
                label = { Text("Player Name") })
        }


        Button(
            onClick = { component.onStartGame() },
        ) {
            Text("Start")
        }
    }

}

enum class NetworkMode {
    LOCAL,
    CLIENT,
    HOST
}