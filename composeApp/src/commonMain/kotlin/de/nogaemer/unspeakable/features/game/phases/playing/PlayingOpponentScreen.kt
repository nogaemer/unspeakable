package de.nogaemer.unspeakable.features.game.phases.playing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import com.composables.icons.lucide.Bomb
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircleWarning
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.i18n.SabotageGameModeStrings
import de.nogaemer.unspeakable.core.mode.modes.SabotageMode
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData

/**
 * Renders the active round UI with timer, card content, and outcome actions.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayingOpponentScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
) {
    val strings = LocalStrings.current
    val sabotageStrings = strings.game.gameModesStrings[SabotageMode().id]!! as SabotageGameModeStrings

    var showSabotageSheet by rememberSaveable { mutableStateOf(false) }
    var sabotageWord by rememberSaveable { mutableStateOf("") }
    val sabotageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            if (state.match?.settings?.enabledModeIds?.contains(SabotageMode().id) ?: false) Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { showSabotageSheet = true },
                    modifier = Modifier.height(96.dp).fillMaxWidth(),
                    shapes = ButtonDefaults.shapesFor(96.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Lucide.Bomb,
                            contentDescription = null,
                        )
                        Text(
                            text = sabotageStrings.title,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                }
            }
        },
    )

    if (showSabotageSheet) {
        val trimmedSabotageWord = sabotageWord.trim()

        ModalBottomSheet(
            onDismissRequest = { showSabotageSheet = false },
            sheetState = sabotageSheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = sabotageStrings.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = sabotageStrings.sheetDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = RoundedCornerShape(28.dp),
                            )
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicTextField(
                            value = sabotageWord,
                            onValueChange = { nextValue ->
                                if (nextValue.length <= SabotageWordLimit) {
                                    sabotageWord = nextValue
                                }
                            },
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Normal,
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (trimmedSabotageWord.isNotEmpty()) {
                                        onEvent(GameClientEvent.Sabotage(trimmedSabotageWord))
                                        sabotageWord = ""
                                        showSabotageSheet = false
                                    }
                                }
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (sabotageWord.isEmpty()) {
                                        Text(
                                            text = sabotageStrings.wordPlaceholder,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.3f
                                            ),
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        text = sabotageStrings.wordCount(sabotageWord.length, SabotageWordLimit),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                    )
                }


                Button(
                    onClick = {
                        onEvent(GameClientEvent.Sabotage(trimmedSabotageWord))
                        sabotageWord = ""
                        showSabotageSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    enabled = trimmedSabotageWord.isNotEmpty() && sabotageWord.length <= SabotageWordLimit,
                    shapes = ButtonDefaults.shapesFor(64.dp),
                ) {
                        Text(
                            text = sabotageStrings.sendButtonText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    device = "spec:width=375dp,height=775dp,orientation=portrait"
)
@Composable
private fun PlayingOpponentScreenPreview() {
    val lyricist = rememberStrings()
    ProvideStrings(lyricist) {
        AppTheme {
            PlayingOpponentScreen(
                state = GameStatePreviewData.playing,
                onEvent = {},
            )
        }
    }
}

private const val SabotageWordLimit = 20
