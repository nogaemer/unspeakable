package de.nogaemer.unspeakable.core.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.decodeToImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Groups image helpers for avatar placeholders and serialization.
 */
class ImageUtils {
    companion object {

        @Composable
        fun createProfilePicture(): ImageBitmap {
            val size = 64
            val bgColor = MaterialTheme.colorScheme.primary

            val bitmap = ImageBitmap(size, size)
            val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

            val bgPaint = Paint().apply { color = bgColor }
            canvas.drawRect(
                left = 0f, top = 0f,
                right = size.toFloat(), bottom = size.toFloat(),
                paint = bgPaint
            )

            return bitmap
        }


        @OptIn(ExperimentalEncodingApi::class)
        fun imageToBase64(image: ImageBitmap): String {
            val pixels = IntArray(image.width * image.height)
            image.readPixels(buffer = pixels)

            val bmpBytes = encodeAs32BitBmp(
                width = image.width,
                height = image.height,
                pixels = pixels,
            )

            return Base64.Default.encode(bmpBytes)
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun base64ToImage(base64: String): ImageBitmap {
            val fallback = ImageBitmap(1, 1)
            return runCatching {
                val bytes = Base64.Default.decode(base64)
                bytes.decodeToImageBitmap()
            }.getOrElse { fallback }
        }

        private fun encodeAs32BitBmp(width: Int, height: Int, pixels: IntArray): ByteArray {
            val fileHeaderSize = 14
            val dibHeaderSize = 40
            val pixelBytes = width * height * 4
            val totalSize = fileHeaderSize + dibHeaderSize + pixelBytes

            val out = ByteArray(totalSize)
            var index = 0

            fun putByte(value: Int) {
                out[index++] = value.toByte()
            }

            fun putShortLE(value: Int) {
                putByte(value and 0xFF)
                putByte((value ushr 8) and 0xFF)
            }

            fun putIntLE(value: Int) {
                putByte(value and 0xFF)
                putByte((value ushr 8) and 0xFF)
                putByte((value ushr 16) and 0xFF)
                putByte((value ushr 24) and 0xFF)
            }

            putByte('B'.code)
            putByte('M'.code)
            putIntLE(totalSize)
            putShortLE(0)
            putShortLE(0)
            putIntLE(fileHeaderSize + dibHeaderSize)

            putIntLE(dibHeaderSize)
            putIntLE(width)
            putIntLE(-height)
            putShortLE(1)
            putShortLE(32)
            putIntLE(0)
            putIntLE(pixelBytes)
            putIntLE(2835)
            putIntLE(2835)
            putIntLE(0)
            putIntLE(0)

            for (pixel in pixels) {
                val r = pixel and 0xFF
                val g = (pixel ushr 8) and 0xFF
                val b = (pixel ushr 16) and 0xFF
                val a = (pixel ushr 24) and 0xFF

                putByte(b)
                putByte(g)
                putByte(r)
                putByte(a)
            }

            return out
        }
    }
}