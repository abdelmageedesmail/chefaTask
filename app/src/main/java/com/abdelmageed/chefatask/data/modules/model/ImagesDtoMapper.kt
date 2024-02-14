package com.abdelmageed.chefatask.data.modules.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagesDtoMapper(
    var id: Int? = null,
    val upc: String? = null,
    val imageUrl: String,
    val title: String,
    val date: String,
    val bitmap: Bitmap? = null
) : Parcelable
