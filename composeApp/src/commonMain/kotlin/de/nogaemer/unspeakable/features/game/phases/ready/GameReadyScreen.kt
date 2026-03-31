package de.nogaemer.unspeakable.features.game.phases.ready

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexTitleVariation
import de.nogaemer.unspeakable.features.game.GameState

/**
 * Shows pre-round readiness UI for the active explainer and waiting players.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GameReadyScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
) {
    val isMyTurn = state.currentExplainer?.id == state.me?.id
    val text = strings.gameReady
    val commonText = strings.common

    Column(
        Modifier.fillMaxSize().padding(16.dp).background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 128.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isMyTurn) {
                Text(
                    text = text.readyTitle,
                    style = TextStyle(
                        fontSize = 72.sp,
                        fontFamily = robotoFlex(robotoFlexTitleVariation()),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                )
                Text(
                    text = text.readySubtitle,
                    style = MaterialTheme.typography.titleLarge,
                )
            } else {

                Text(
                    text = text.waitingFor,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = state.currentExplainer?.name ?: "...",
                    style = TextStyle(
                        fontSize = 72.sp,
                        fontFamily = robotoFlex(robotoFlexTitleVariation()),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                )
            }

        }

        if (isMyTurn) Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 128.dp),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(GameClientEvent.ReadyToStartMyTurn) },
                shapes = ButtonDefaults.shapesFor(96.dp),
                contentPadding = ButtonDefaults.ExtraLargeContentPadding,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Lucide.Play,
                        contentDescription = text.startTurnDescription,
                    )
                    Text(
                        text = commonText.start,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}