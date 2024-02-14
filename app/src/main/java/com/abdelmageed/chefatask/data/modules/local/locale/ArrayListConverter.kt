package com.abdelmageed.chefatask.data.modules.local.locale

import androidx.room.TypeConverter
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.google.gson.Gson


class ArrayListConverter {
    @TypeConverter
    fun listToJsonString(value: List<ImagesDtoMapper>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) =
        Gson().fromJson(value, Array<ImagesDtoMapper>::class.java).toMutableList()
}