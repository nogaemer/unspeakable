package de.nogaemer.unspeakable.features.game.phases.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexTitleVariation

/**
 * Shows the disconnect state and provides a route back to home.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectionLostScreen(
    onGoHome: () -> Unit,
) {
    val text = strings.connectionLost

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 128.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text.title,
                style = TextStyle(
                    fontSize = 72.sp,
                    fontFamily = robotoFlex(robotoFlexTitleVariation()),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            )
            Text(
                text = text.description,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 128.dp),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onGoHome,
                shapes = ButtonDefaults.shapesFor(96.dp),
                contentPadding = ButtonDefaults.ExtraLargeContentPadding,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = text.backToHome,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}

@Composable
@Preview()
fun ConnectionLostScreenPreview() {
    MaterialTheme {
        ConnectionLostScreen(onGoHome = {})
    }
}

