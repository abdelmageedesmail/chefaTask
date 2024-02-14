package com.abdelmageed.chefatask.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

object ImageColor {

    fun getDominantColor(bitmap: Bitmap?): Int {
        if (bitmap == null) {
            return Color.TRANSPARENT
        }
        val width = bitmap.width
        val height = bitmap.height
        val size = width * height
        val pixels = IntArray(size)
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var color: Int
        var r = 0
        var g = 0
        var b = 0
        var a: Int
        var count = 0
        for (i in pixels.indices) {
            color = pixels[i]
            a = Color.alpha(color)
            if (a > 0) {
                r += Color.red(color)
                g += Color.green(color)
                b += Color.blue(color)
                count++
            }
        }
        r /= count
        g /= count
        b /= count
        r = r shl 16 and 0x00FF0000
        g = g shl 8 and 0x0000FF00
        b = b and 0x000000FF
        color = -0x1000000 or r or g or b
        Log.e("rgbColor", "$r..$g..$b")
        return color
    }

    fun isDarkColor(color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
                color
            )) / 255
        return darkness >= 0.5
    }

}