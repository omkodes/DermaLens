package com.example.dermalens.ml

import android.graphics.Bitmap
import java.nio.FloatBuffer
import java.nio.ShortBuffer

object ImagePreprocessor {

    /**
     * Crops the center square of the bitmap to maintain 1:1 aspect ratio,
     * then scales it to [targetSize]x[targetSize] (default 224x224).
     */
    fun cropCenterSquareAndResize(source: Bitmap, targetSize: Int = 224): Bitmap {
        val dimension = minOf(source.width, source.height)
        val xOffset = (source.width - dimension) / 2
        val yOffset = (source.height - dimension) / 2
        val cropped = Bitmap.createBitmap(source, xOffset, yOffset, dimension, dimension)
        return if (dimension == targetSize) {
            cropped
        } else {
            Bitmap.createScaledBitmap(cropped, targetSize, targetSize, true)
        }
    }

    /**
     * Preprocesses bitmap into an NCHW [1, 3, height, width] FloatBuffer with ImageNet normalization.
     */
    fun preprocessBitmapToFloat32Buffer(
        bitmap: Bitmap,
        width: Int = 224,
        height: Int = 224
    ): FloatBuffer {
        val buffer = FloatBuffer.allocate(1 * 3 * height * width)
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)

        // NCHW layout: Channel 0 (R), Channel 1 (G), Channel 2 (B)
        for (c in 0..2) {
            for (i in 0 until width * height) {
                val pixel = pixels[i]
                val channelVal = when (c) {
                    0 -> ((pixel shr 16) and 0xFF) / 255.0f // Red
                    1 -> ((pixel shr 8) and 0xFF) / 255.0f  // Green
                    else -> (pixel and 0xFF) / 255.0f       // Blue
                }
                buffer.put((channelVal - mean[c]) / std[c])
            }
        }
        buffer.rewind()
        return buffer
    }

    /**
     * Converts normalized Float32 values into IEEE 754 half-precision (Float16) ShortBuffer.
     */
    fun preprocessBitmapToFloat16Buffer(
        bitmap: Bitmap,
        width: Int = 224,
        height: Int = 224
    ): ShortBuffer {
        val floatBuf = preprocessBitmapToFloat32Buffer(bitmap, width, height)
        val shortBuf = ShortBuffer.allocate(floatBuf.capacity())
        while (floatBuf.hasRemaining()) {
            shortBuf.put(floatToHalf(floatBuf.get()))
        }
        shortBuf.rewind()
        return shortBuf
    }

    /**
     * IEEE 754 single-precision float to half-precision 16-bit short conversion.
     */
    fun floatToHalf(f: Float): Short {
        val fbits = java.lang.Float.floatToIntBits(f)
        val sign = (fbits ushr 16) and 0x8000
        var valExp = (fbits ushr 23) and 0x00FF
        var mantissa = fbits and 0x007FFFFF

        if (valExp == 255) return (sign or 0x7C00 or (if (mantissa != 0) 1 else 0)).toShort()
        valExp -= 127 - 15
        if (valExp >= 31) return (sign or 0x7C00).toShort()
        if (valExp <= 0) {
            if (valExp < -10) return sign.toShort()
            mantissa = (mantissa or 0x00800000) shr (1 - valExp)
            return (sign or (mantissa shr 13)).toShort()
        }
        return (sign or (valExp shl 10) or (mantissa shr 13)).toShort()
    }

    /**
     * IEEE 754 half-precision 16-bit short to single-precision float conversion.
     */
    fun halfToFloat(hbits: Short): Float {
        val mant = hbits.toInt() and 0x03FF
        val exp = hbits.toInt() and 0x7C00
        if (exp == 0x7C00) {
            return java.lang.Float.intBitsToFloat(((hbits.toInt() and 0x8000) shl 16) or 0x7F800000 or (mant shl 13))
        } else if (exp != 0) {
            val newExp = (exp shr 10) + (127 - 15)
            return java.lang.Float.intBitsToFloat(((hbits.toInt() and 0x8000) shl 16) or (newExp shl 23) or (mant shl 13))
        } else if (mant != 0) {
            var m = mant
            var e = 1 - 15 + 127
            while ((m and 0x0400) == 0) {
                m = m shl 1
                e -= 1
            }
            m = m and 0x03FF
            return java.lang.Float.intBitsToFloat(((hbits.toInt() and 0x8000) shl 16) or (e shl 23) or (m shl 13))
        }
        return java.lang.Float.intBitsToFloat((hbits.toInt() and 0x8000) shl 16)
    }
}
