package de.nogaemer.unspeakable.features.game.phases.playing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bomb
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircleWarning
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData

/**
 * Renders the active round UI with timer, card content, and outcome actions.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayingOpponentScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
) {
    PlayingRoundContent(
        state = state,
        forbiddenTrailingContent = { word ->
            Icon(
                imageVector = Lucide.MessageCircleWarning,
                contentDescription = null,
                modifier = Modifier.requiredSize(24.dp).clickable {
                    onEvent(GameClientEvent.CardWrongByOpponent(word))
                },
                tint = MaterialTheme.colorScheme.error,
            )
        },
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { onEvent(GameClientEvent.CardCorrect) },
                    modifier = Modifier.height(96.dp).fillMaxWidth(),
                    shapes = ButtonDefaults.shapesFor(96.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Lucide.Bomb,
                            contentDescription = null,
                        )
                        Text(
                            text = "Sabotage",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                }
            }
        },
    )
}




@Preview(
    showBackground = true,
    device = "spec:width=375dp,height=775dp,orientation=portrait"
)
@Composable
private fun PlayingOpponentScreenPreview() {
    AppTheme {
        PlayingOpponentScreen(
            state = GameStatePreviewData.playing,
            onEvent = {},
        )
    }
}
