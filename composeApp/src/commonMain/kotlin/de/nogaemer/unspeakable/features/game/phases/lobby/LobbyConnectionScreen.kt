package de.nogaemer.unspeakable.features.game.phases.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.ClipboardCopy
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.qr.QrGeneratorView
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.util.getRandomTestIp
import de.nogaemer.unspeakable.core.util.ipToCode

@Composable
fun LobbyConnectionScreen(
    qrData: String = ipToCode(getRandomTestIp()),
    modifier: Modifier = Modifier,
    onCopyClicked: () -> Unit = {},
) {
    val text = strings
    val colorScheme = MaterialTheme.colorScheme


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text.qr.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text.qr.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.clip(RoundedCornerShape(28.dp))
                .background(colorScheme.surfaceContainerHigh)
                .padding(24.dp),
        ) {
            QrGeneratorView(qrData, colorScheme, Modifier.size(220.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        SegmentedColumn(contentPadding = PaddingValues(0.dp)) {
            SegmentedListItem(
                colors = ListItemDefaults.colors(
                    containerColor = colorScheme.surfaceContainerHigh,
                ),
                headlineContent = {
                    Text(
                        text = "Code",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                },
                supportingContent = {
                    Text(
                        text = qrData,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                trailingContent = {
                    Button(
                        onClick = onCopyClicked,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = Lucide.ClipboardCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = text.common.copy, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun QrGeneratorViewPreview() {
    AppTheme(
        darkTheme = true
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface
        ) {
            LobbyConnectionScreen(qrData = "4LF2G4G")
        }

    }
}