package de.nogaemer.unspeakable.features.game.phases.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CircleDashed
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.TeamPoints
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.model.CardOutcome
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexTitleVariation
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoundOverviewScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
) {
    val text = strings.roundOverview
    val gameText = strings.game

    Scaffold(
        floatingActionButton = {
            MediumFloatingActionButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = Lucide.ArrowRight,
                    contentDescription = null,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background).padding(16.dp)
                .padding(top = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text.timeUpTitle,
                style = TextStyle(
                    fontSize = 72.sp,
                    fontFamily = robotoFlex(robotoFlexTitleVariation()),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(200.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialShapes.Cookie6Sided.toShape()
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = state.currentRound?.correct.toString(),
                        style = TextStyle(
                            fontSize = 96.sp,
                            fontFamily = robotoFlex(
                                FontVariation.Settings(
                                    FontVariation.slant(-10f),
                                    FontVariation.opticalSizing(96.sp),
                                    FontVariation.weight(800),
                                    FontVariation.grade(-200),
                                    FontVariation.Setting("YOPQ", 25f)
                                )
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp),
            ) {
                TeamPoints(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    teamAName = gameText.teamA,
                    teamBName = gameText.teamB,
                    leftTeamPoints = 12,
                    rightTeamPoints = 8,
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SegmentedColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    SegmentedListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Lucide.CircleCheck,
                                contentDescription = null,
                            )
                        },
                        headlineContent = {
                            Text(
                                text = text.correctLabel,
                            )
                        },
                    )

                    SegmentedListItem(
                        headlineContent = {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                state.currentRound?.playedCards?.forEach { card ->
                                    if (card.outcome == CardOutcome.CORRECT) Chip(card.card.word)
                                }
                            }
                        }
                    )
                }

                SegmentedColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    SegmentedListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Lucide.CircleX,
                                contentDescription = null,
                            )
                        },
                        headlineContent = {
                            Text(
                                text = text.wrongLabel,
                            )
                        },
                    )

                    SegmentedListItem(
                        headlineContent = {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                state.currentRound?.playedCards?.forEach { card ->
                                    if (card.outcome == CardOutcome.WRONG) Chip(card.card.word)
                                }
                            }
                        }
                    )
                }
                SegmentedColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    SegmentedListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Lucide.CircleDashed,
                                contentDescription = null,
                            )
                        },
                        headlineContent = {
                            Text(
                                text = text.skippedLabel,
                            )
                        },
                    )

                    SegmentedListItem(
                        headlineContent = {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                state.currentRound?.playedCards?.forEach { card ->
                                    if (card.outcome == CardOutcome.SKIPPED) Chip(card.card.word)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Row(
        Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(start = 12.dp, top = 6.dp, end = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
            )
        )
    }
}


@Preview(
    showSystemUi = true,
)
@Composable
fun RoundOverviewScreenPreview() {
    AppTheme(
        darkTheme = true,
        seedColor = Color(0xFF7BE555),
    ) {
        RoundOverviewScreen(
            GameStatePreviewData.roundSummary,
            { }
        )
    }
}
