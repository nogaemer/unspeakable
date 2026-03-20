package de.nogaemer.unspeakable.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.jetbrains.compose.resources.Font
import unspeakable.composeapp.generated.resources.Res
import unspeakable.composeapp.generated.resources.roboto_flex

@Composable
fun robotoFlex (
    fontVariation: FontVariation.Settings = robotoFlexTitleVariationNarrow()
): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.roboto_flex,
            variationSettings = fontVariation
        )
    )
}

@Composable
fun robotoFlexTitleVariationNarrow (): FontVariation.Settings {
    return FontVariation.Settings(
        FontVariation.slant(-10f),
        FontVariation.grade(-90),
        FontVariation.weight(600),
        FontVariation.width(40f),
        FontVariation.Setting("opsz", 72f),
        FontVariation.Setting("XTRA", 450f),
        FontVariation.Setting("YOPQ", 25f),
    )
}

@Composable
fun robotoFlexTitleVariation (): FontVariation.Settings {
    return FontVariation.Settings(
        FontVariation.slant(-10f),
        FontVariation.grade(-90),
        FontVariation.weight(700),
        FontVariation.width(60f),
        FontVariation.Setting("opsz", 72f),
        FontVariation.Setting("XTRA", 450f),
        FontVariation.Setting("YOPQ", 25f),
    )
}

@Composable
fun robotoFlexClock (): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.roboto_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.Setting("GRAD", -200f),
                FontVariation.Setting("XOPQ", 96f),
                FontVariation.Setting("XTRA", 414f),
                FontVariation.Setting("YOPQ", 79f),
                FontVariation.Setting("wdth", 25f),
                FontVariation.Setting("opsz", 144f),
                FontVariation.Setting("wght", 500f)
            )
        )
    )
}

@Composable
fun robotoFlexCardItems (): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.roboto_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.grade(-90),
                FontVariation.weight(600),
                FontVariation.width(120f),
                FontVariation.Setting("opsz", 16f),
            )
        )
    )
}

