package de.nogaemer.unspeakable.features.game_setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.lyricist.strings
import co.touchlab.kermit.Logger
import com.composables.icons.lucide.Flashlight
import com.composables.icons.lucide.FlashlightOff
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.util.codeToIp
import de.nogaemer.unspeakable.core.util.isValidCode
import kotlinx.coroutines.launch
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JoinLobbyScreen(
    lobbyCode: String,
    onCodeChange: (String) -> Unit,
    onJoin: () -> Unit,
) {
    var flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val canJoin = lobbyCode.length >= 4

    val text = strings
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()).zIndex(0f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = text.qr.scanToJoin,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text.qr.description,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(248.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(48.dp))
                .background(colorScheme.surfaceContainerLowest)
                .border(4.dp, colorScheme.tertiary, RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            QrScanner(
                modifier = Modifier.fillMaxSize(),
                flashlightOn = flashlightOn,
                openImagePicker = openImagePicker,
                onCompletion = { scanned ->
                    try {
                        val ip = codeToIp(scanned)
                        Logger.d("Scanned IP: $ip")
                        if (!isValidCode(scanned)) throw Exception("Invalid IP")

                        onCodeChange(scanned)
                        onJoin()
                    } catch (_: Exception) {
                        Logger.e("Invalid QR code")
                    }
                },
                imagePickerHandler = { openImagePicker = it },
                onFailure = { scope.launch { } },
                cameraLens = CameraLens.Back,
                customOverlay = { OverlayShape.Rectangle }
            )

            IconToggleButton(
                checked = flashlightOn,
                onCheckedChange = { flashlightOn = it },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colorScheme.secondaryContainer)
            ) {
                Icon(
                    imageVector = if (flashlightOn) Lucide.Flashlight else Lucide.FlashlightOff,
                    contentDescription = strings.common.toggleFlashlight,
                    modifier = Modifier.size(22.dp),
                    tint = colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = colorScheme.outlineVariant
            )
            Text(
                text = strings.common.or,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = colorScheme.outlineVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = text.qr.enterCode,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                value = lobbyCode,
                onValueChange = { if (it.length <= 10) onCodeChange(it.uppercase()) },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                ),
                cursorBrush = SolidColor(colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { if (canJoin) onJoin() }
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (lobbyCode.isEmpty()) {
                            Text(
                                text = text.common.code.uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onJoin,
            enabled = canJoin,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 8.dp),
            shapes = ButtonDefaults.shapesFor(64.dp),
        ) {
            Text(
                text = text.qr.joinLobby,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun JoinLobbyScreenPreview() {
    AppTheme(darkTheme = true) {
        JoinLobbyScreen(
            lobbyCode = "4LF2G4G",
            onCodeChange = {},
            onJoin = {},
        )
    }
}
