package de.nogaemer.unspeakable.features.game.phases.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowLeftRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.Settings
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.LoadingScreen
import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.core.model.ProfileShape
import de.nogaemer.unspeakable.core.model.toRoundedPolygon
import de.nogaemer.unspeakable.core.util.ImageUtils
import de.nogaemer.unspeakable.core.util.getLocalIpAddress
import de.nogaemer.unspeakable.core.util.ipToCode
import de.nogaemer.unspeakable.core.util.toClipEntry
import de.nogaemer.unspeakable.features.game.GameState
import de.nogaemer.unspeakable.features.game.preview.GameStatePreviewData
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Main lobby screen for managing players and starting the game.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalUuidApi::class
)
@Composable
fun LobbyScreen(
    state: GameState,
    onEvent: (event: GameClientEvent) -> Unit,
    onEventAs: (GameClientEvent, String) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit = {},
) {
    if (state.match == null) return LoadingScreen()

    val me = state.me?.id ?: ""
    val text = strings.gameLobby
    val isLocal = state.isLocalGame

    val lobbyCode = remember {
        val ip = (if (state.isHost) getLocalIpAddress() else state.hostIp) ?: "127.0.0.1"
        ipToCode(ip)
    }
    val clipboardManager = LocalClipboard.current
    var showConnectionSheet by remember { mutableStateOf(false) }
    var addingToTeamId by remember { mutableStateOf<String?>(null) }
    var nameInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val profilePicture = ImageUtils.createProfilePictureByHue(Random.nextFloat()*360)

    // Add player dialog
    addingToTeamId?.let { teamId ->
        AlertDialog(
            onDismissRequest = { addingToTeamId = null; nameInput = "" },
            title = {
                val teamName = state.match.teams.firstOrNull { it.id == teamId }?.name ?: ""
                Text(strings.gameLobby.addPlayerTitle(teamName))
            },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(strings.gameLobby.nameLabel) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done,
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    enabled = nameInput.isNotBlank(),
                    onClick = {
                        onEvent(
                            GameClientEvent.AddLocalPlayer(
                                player = Player(
                                    id = "local_${Uuid.random()}",
                                    name = nameInput.trim(),
                                    isHost = false,
                                    profilePicture = ProfilePicture(
                                        ProfileShape.entries.random(),
                                        ImageUtils.imageToBase64(profilePicture)
                                    ),
                                    teamId = teamId,
                                ),
                            )
                        )
                        addingToTeamId = null
                        nameInput = ""
                    },
                ) { Text(strings.gameLobby.addButton) }
            },
            dismissButton = {
                TextButton(onClick = { addingToTeamId = null; nameInput = "" }) {
                    Text(strings.common.cancel)
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text.lobbyTitle) },
                navigationIcon = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = { PlainTooltip { Text(strings.common.back) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(16.dp).size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                .zIndex(1f),
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = strings.common.back,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
                actions = {
                    Row {
                        // QR code only makes sense in network mode
                        if (!isLocal) {
                            IconButton(
                                onClick = { showConnectionSheet = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                )
                            ) {
                                Icon(Lucide.QrCode, contentDescription = text.lobbySettingsDescription)
                            }
                        }
                        if (state.isHost) {
                            IconButton(
                                onClick = onOpenSettings,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                )
                            ) {
                                Icon(Lucide.Settings, contentDescription = text.lobbySettingsDescription)
                            }
                        }
                    }
                },
            )
        }
    ) { innerPadding ->

        if (showConnectionSheet) {
            ModalBottomSheet(
                modifier = Modifier.wrapContentHeight(),
                onDismissRequest = { showConnectionSheet = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
            ) {
                LobbyConnectionScreen(
                    qrData = lobbyCode,
                    onCopyClicked = {
                        scope.launch { clipboardManager.setClipEntry(lobbyCode.toClipEntry()) }
                    },
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                state.match.teams.forEach { team ->
                    val otherTeam = state.match.teams.first { it.id != team.id }
                    PlayersContainer(
                        title = team.name,
                        players = team.players,
                        me = me,
                        isLocalGame = isLocal,
                        errorMessage = text.noPlayersInTeam(team.name),
                        onJoinTeam = { onEvent(GameClientEvent.JoinTeam(team)) },
                        onAddLocalPlayer = { addingToTeamId = team.id },
                        onMovePlayer = { player ->
                            onEventAs(GameClientEvent.JoinTeam(otherTeam), player.id)
                        },
                    )
                }
            }

            if (state.isHost) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 28.dp)
                        .padding(bottom = 16.dp),
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(96.dp),
                        onClick = { onEvent(GameClientEvent.StartGame) },
                        shapes = ButtonDefaults.shapesFor(96.dp),
                    ) {
                        Text(
                            text = text.startGame,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
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
    errorMessage: String,
    players: List<Player>,
    me: String,
    isLocalGame: Boolean,
    onJoinTeam: () -> Unit = {},
    onAddLocalPlayer: () -> Unit = {},
    onMovePlayer: (Player) -> Unit = {},
) {
    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            if (players.isEmpty()) Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }
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
                PlayerCard(
                    player = player,
                    isMe = player.id == me,
                    isLocalGame = isLocalGame,
                    onMove = { onMovePlayer(player) },
                )
            }

            when {
                isLocalGame -> AddLocalPlayerCard(onAddLocalPlayer)
                players.none { it.id == me } -> JoinTeamCard(onJoinTeam)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlayerCard(
    player: Player,
    isMe: Boolean = false,
    isLocalGame: Boolean = false,
    onMove: () -> Unit = {},
) {
    val profilePicture = player.profilePicture
    val image = ImageUtils.base64ToImage(profilePicture.image)
    val colors = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
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
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (isLocalGame) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { onMove() },
                    color = colors.primaryContainer,
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeftRight,
                        contentDescription = strings.gameLobby.moveToOtherTeamDescription,
                        tint = colors.onPrimaryContainer,
                        modifier = Modifier.padding(3.dp),
                    )
                }
            }
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
private fun AddLocalPlayerCard(onClick: () -> Unit) {
    val text = strings.gameLobby
    val colors = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onClick() }
                .padding(8.dp)
                .size(48.dp),
            shape = CircleShape,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        drawCircle(
                            color = colors.outline,
                            radius = size.minDimension / 2f - strokeWidth / 2f,
                            style = Stroke(
                                width = strokeWidth,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(8.dp.toPx(), 8.dp.toPx())
                                )
                            )
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = text.addPlayerDescription,
                    tint = colors.onSurfaceVariant,
                )
            }
        }
        Text(
            color = colors.onSurfaceVariant,
            text = text.addButton,
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
                    contentDescription = text.joinTeamDescription,
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

@Preview(showSystemUi = true)
@Composable
fun LobbyScreenPreview() {
    AppTheme {
        LobbyScreen(
            state = GameStatePreviewData.lobby,
            onEvent = {},
            onEventAs = {_, _ ->},
            onOpenSettings = {},
        )
    }
}
