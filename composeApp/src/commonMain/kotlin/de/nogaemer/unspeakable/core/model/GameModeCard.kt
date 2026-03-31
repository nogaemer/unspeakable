package de.nogaemer.unspeakable.core.model

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.util.robotoFlex
import de.nogaemer.unspeakable.core.util.robotoFlexTitleVariationNarrow

/**
 * Renders the large stylized game-mode card used on primary selection screens.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GameModeCard(
    title: String,
    fontVariation: FontVariation.Settings = robotoFlexTitleVariationNarrow(),
    backgroundShapeModifier: Modifier,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shapeColor = lerp(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surfaceContainer, 0.3f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
    ) {

        Surface(
            modifier = backgroundShapeModifier
                .align(Alignment.CenterStart),
            color = shapeColor,
        ) {
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.ArrowRight,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }


        OutlinedText(
            text = title,
            fontSize = 64.sp,
            lineHeight = 64.sp,
            letterSpacing = (-2).sp,
            fontFamily = robotoFlex(
                fontVariation
            ),
            fillColor = shapeColor,
            outlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
            outlineDrawStyle = Stroke(
                width = 6f
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
}

/**
 * Renders a compact game-mode card variant for dense lists.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GameModeCardSmall(
    title: String,
    fontVariation: FontVariation.Settings = robotoFlexTitleVariationNarrow(),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .padding(16.dp, 8.dp, 16.dp, 8.dp),
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSecondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.ArrowRight,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }


        Text(
            text = title,
            fontSize = 64.sp,
            lineHeight = 64.sp,
            letterSpacing = (-2).sp,
            fontFamily = robotoFlex(
                fontVariation
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.CenterStart),
        )
    }
}


// Source - https://stackoverflow.com/a/78658641
// Posted by tyg, modified by community. See post 'Timeline' for change history
// Retrieved 2026-03-09, License - CC BY-SA 4.0

/**
 * Draws outlined text by layering stroke and fill text passes.
 */
@ExperimentalComposeUiApi
@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.Unspecified,
    outlineColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    outlineDrawStyle: Stroke = Stroke(),
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier.semantics { hideFromAccessibility() },
            color = outlineColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = null,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style.copy(
                shadow = null,
                drawStyle = outlineDrawStyle,
            ),
        )

        Text(
            text = text,
            color = fillColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
}



