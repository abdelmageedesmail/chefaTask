package com.abdelmageed.chefatask.extension

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.ByteArrayOutputStream

fun Bitmap.resizeImage(newWidth:Int,newHeight: Int,quality:Int):ByteArray{
    val matrix = Matrix()
    matrix.postScale(
        newWidth.toFloat() / this.width,
        newHeight.toFloat() / this.height
    )

    val resizedBitmap = Bitmap.createBitmap(
        this,
        0,
        0,
        this.width,
        this.height,
        matrix,
        false
    )

    // Compress the resized bitmap
    val stream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

    return stream.toByteArray()
}