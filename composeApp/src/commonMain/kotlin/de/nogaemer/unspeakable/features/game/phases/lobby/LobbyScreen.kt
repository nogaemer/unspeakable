package de.nogaemer.unspeakable.features.game.phases.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Settings
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.toRoundedPolygon
import de.nogaemer.unspeakable.core.util.ImageUtils
import de.nogaemer.unspeakable.features.game.GameState

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
    onOpenSettings: () -> Unit,
) {
    if (state.match == null) return LoadingIndicator()
    val me = state.me?.id ?: ""

    val text = strings.gameLobby

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lobby") },
                actions = {
                    if (state.isHost) {
                        IconButton(onClick = onOpenSettings) {
                            Icon(Lucide.Settings, contentDescription = "Lobby settings")
                        }
                    }
                }

            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column{
                state.match.teams.forEach { team ->
                    PlayersContainer(
                        title = team.name,
                        players = team.players,
                        me = me,
                        onJoinTeam = { onEvent(GameClientEvent.JoinTeam(team)) }
                    )
                }

            }

            if (state.isHost) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 28.dp).padding(bottom = 16.dp),
                ) {
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth().height(96.dp),
                        onClick = { onEvent(GameClientEvent.StartGame) },
                    ) {
                        Text(
                            text = text.startGame,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayersContainer(
    title: String,
    players: List<Player>,
    me: String,
    onJoinTeam: () -> Unit = {},
) {
    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {
            players.forEach { player ->
                PlayerCard(player = player, isMe = player.id == me)
            }
            if (players.find { it.id == me } == null) {
                JoinTeamCard(onJoinTeam)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlayerCard(player: Player, isMe: Boolean = false) {
    val profilePicture = player.profilePicture
    val image = ImageUtils.base64ToImage(profilePicture.image)

    val colors = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .clip(profilePicture.shape.toRoundedPolygon().toShape()),
        ) {
            Image(
                bitmap = image,
                contentDescription = player.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            modifier = if (isMe) Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(colors.primaryContainer)
                .padding(horizontal = 4.dp)
            else Modifier,
            color = if (isMe) colors.onPrimaryContainer else colors.onSurfaceVariant,
            text = player.name,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun JoinTeamCard(
    onJoinTeam: () -> Unit = {},
) {
    val text = strings.gameLobby
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.clip(CircleShape)
                .clickable { onJoinTeam() }
                .padding(8.dp)
                .size(48.dp),
            shape = CircleShape,
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        drawCircle(
                            color = colors.outline,
                            radius = size.minDimension / 2f - strokeWidth / 2f,
                            style = Stroke(
                                width = strokeWidth,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(
                                        8.dp.toPx(),
                                        8.dp.toPx()
                                    )
                                )
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Join team",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            color = colors.onSurfaceVariant,
            text = text.joinTeam,
            fontSize = 14.sp,
        )
    }
}
