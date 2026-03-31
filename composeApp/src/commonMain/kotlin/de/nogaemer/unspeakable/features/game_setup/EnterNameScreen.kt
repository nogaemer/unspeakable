package de.nogaemer.unspeakable.features.game_setup

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EnterNameScreen(
    playerName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = strings
    val canConfirm = playerName.isNotBlank()

    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeBottom > 0
    val bottomPadding by animateDpAsState(
        targetValue = if (isKeyboardVisible) 40.dp else 24.dp,
        label = "bottomPadding"
    )

    val topPadding by animateDpAsState(
        targetValue = if (isKeyboardVisible) 0.dp else 64.dp,
        label = "topPadding"
    )

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp + topPadding))

            Text(
                text = s.gameSetup.whatsYourName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = playerName,
                    onValueChange = {
                        if (it.length <= 20) onNameChange(it)
                    },
                    textStyle = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { if (canConfirm) onConfirm() }
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (playerName.isEmpty()) {
                                Text(
                                    text = s.gameSetup.typePlaceholder,
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shapes = ButtonDefaults.shapesFor(64.dp),
            ) {
                Text(
                    text = s.common.next,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(bottomPadding))
        }
    }
}


@Preview
@Composable
fun EnterNameScreenPreview() {
    val lyricist = rememberStrings()
    ProvideStrings(lyricist) {
        AppTheme {
            EnterNameScreen(
                playerName = "",
                onNameChange = {},
                onConfirm = {}
            )
        }
    }
}
