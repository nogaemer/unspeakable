package de.nogaemer.unspeakable.core.components.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import qrgenerator.qrkitpainter.PatternType
import qrgenerator.qrkitpainter.QrBallType
import qrgenerator.qrkitpainter.QrFrameType
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.QrKitShapes
import qrgenerator.qrkitpainter.QrPixelType
import qrgenerator.qrkitpainter.getSelectedFrameShape
import qrgenerator.qrkitpainter.getSelectedPattern
import qrgenerator.qrkitpainter.getSelectedPixel
import qrgenerator.qrkitpainter.getSelectedQrBall
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush


/**
 * Renders a stylized QR code for sharing lobby join data.
 */
@Composable
fun QrGeneratorView(
    data: String,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
) {
    val painter = rememberQrKitPainter(data) {
        shapes = QrKitShapes(
            ballShape = getSelectedQrBall(QrBallType.RoundCornersQrBall(radius = 0.2f)),
            darkPixelShape = getSelectedPixel(QrPixelType.RoundCornerPixel(radius = 0.5f)),
            frameShape = getSelectedFrameShape(QrFrameType.RoundCornersFrame(corner = 0.3f)),
            codeShape = getSelectedPattern(PatternType.SquarePattern),
        )
        colors = QrKitColors(
            darkBrush = QrKitBrush.solidBrush(colorScheme.onSurface),
            frameBrush = QrKitBrush.solidBrush(colorScheme.primary),
            ballBrush = QrKitBrush.solidBrush(colorScheme.onSurface),
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
