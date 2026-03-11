package de.nogaemer.unspeakable.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nogaemer.unspeakable.core.util.robotoFlex

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, bottom = 0.dp),
        verticalArrangement = Arrangement.spacedBy(72.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                fontSize = 72.sp,
                text = "Unspeakable".uppercase(),
                fontFamily = robotoFlex(
                    FontVariation.Settings(
                        FontVariation.slant(-10f),
                        FontVariation.width(45f),
                        FontVariation.weight(700),
                        FontVariation.grade(-90),
                        FontVariation.Setting("opsz", 72f),
                        FontVariation.Setting("YOPQ", 25f),
                    )
                ),
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
            )
            Text(
                fontSize = 22.sp,
                text = "THE ULTIMATE WORD GAME",
                fontFamily = robotoFlex(
                    FontVariation.Settings(
                        FontVariation.weight(400),
                    )
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GameModeCard(
                title = "Host  a\nGame",
                fontVariation = FontVariation.Settings(
                    FontVariation.weight(500),
                    FontVariation.width(100f),
                    FontVariation.grade(150),
                    FontVariation.opticalSizing(64.sp),
                    FontVariation.Setting("XOPQ", 175f),
                    FontVariation.Setting("YOPQ", 90f)
                ),
                backgroundShapeModifier = Modifier
                    .offset(x = (-65).dp, y = 20.dp)
                    .rotate(20F)
                    .requiredSize(320.dp).aspectRatio(1f)
                    .clip(MaterialShapes.Cookie7Sided.toShape()),
                onClick = component::onHostClicked
            )

            GameModeCard(
                title = "Join  a\nGame",
                fontVariation = FontVariation.Settings(
                    FontVariation.slant(-10f),
                    FontVariation.weight(400),
                    FontVariation.width(100f),
                    FontVariation.grade(-200),
                    FontVariation.opticalSizing(64.sp),
                    FontVariation.Setting("XOPQ", 175f),
                    FontVariation.Setting("YTUC", 760f)
                ),
                backgroundShapeModifier = Modifier
                    .offset(x = (-65).dp, y = 20.dp)

                    .rotate(-90F)
                    .requiredSize(320.dp).aspectRatio(1f)
                    .clip(MaterialShapes.Ghostish.toShape()),
                onClick = component::onJoinClicked
            )

            GameModeCardSmall(
                title = "Local",
                fontVariation = FontVariation.Settings(
                    FontVariation.weight(500),
                    FontVariation.grade(150),
                    FontVariation.opticalSizing(64.sp),
                    FontVariation.Setting("XOPQ", 175f),
                    FontVariation.Setting("YOPQ", 100f)
                ),
                onClick = component::onLocalClicked
            )
        }
    }
}