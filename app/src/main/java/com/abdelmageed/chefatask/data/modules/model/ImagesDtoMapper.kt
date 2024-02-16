package com.abdelmageed.chefatask.data.modules.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagesDtoMapper(
    var id: Int? = null,
    val imageUrl: String,
    val title: String,
    val date: String? = null,
    val bitmap: Bitmap? = null,
    var bufferArray: ByteArray? = null,
) : Parcelable
