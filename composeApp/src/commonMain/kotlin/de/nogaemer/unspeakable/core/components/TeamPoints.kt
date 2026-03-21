package de.nogaemer.unspeakable.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeamPoints(
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
    teamAName: String,
    teamBName: String,
    leftTeamPoints: Int,
    rightTeamPoints: Int,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        //Left Team indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 8.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
            ) {
                Text(
                    teamAName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier.size(40.dp).clip(MaterialShapes.Pentagon.toShape())
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    leftTeamPoints.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        //Right Team indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(MaterialShapes.Pentagon.toShape())
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    rightTeamPoints.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 12.dp,
                            bottomStart = 8.dp,
                            bottomEnd = 12.dp
                        )
                    )
                    .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
            ) {
                Text(
                    teamBName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}