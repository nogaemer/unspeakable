package de.nogaemer.unspeakable.features.game.phases.playing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SkipForward
import com.composables.icons.lucide.X
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData

/**
 * Renders the active round UI with timer, card content, and outcome actions.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayingExplainerScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
) {
    PlayingRoundContent(
        state = state,
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalButton(
                    onClick = { onEvent(GameClientEvent.CardWrong) },
                    modifier = Modifier.requiredSize(96.dp),
                    shapes = ButtonDefaults.shapesFor(96.dp),
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { onEvent(GameClientEvent.CardCorrect) },
                    modifier = Modifier.requiredSize(96.dp),
                    shapes = ButtonDefaults.shapes(
                        shape = RoundedCornerShape(28.dp),
                        ButtonDefaults.extraLargePressedShape,
                    ),
                ) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = null,
                    )
                }
                FilledTonalButton(
                    onClick = { onEvent(GameClientEvent.CardSkipped) },
                    modifier = Modifier.requiredSize(96.dp),
                    shapes = ButtonDefaults.shapesFor(96.dp),
                ) {
                    Icon(
                        imageVector = Lucide.SkipForward,
                        contentDescription = null,
                    )
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
private fun PlayingScreenPreview() {
    AppTheme {
        PlayingExplainerScreen(
            state = GameStatePreviewData.playing,
            onEvent = {},
        )
    }
}
