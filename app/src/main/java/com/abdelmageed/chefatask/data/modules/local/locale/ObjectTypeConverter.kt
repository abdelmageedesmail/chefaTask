package com.abdelmageed.chefatask.data.modules.local.locale

import androidx.room.TypeConverter
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ObjectTypeConverter {
    @TypeConverter
    fun toHashMap(value: String): ImagesDtoMapper =
        Gson().fromJson(value, object : TypeToken<ImagesDtoMapper>() {}.type)

    @TypeConverter
    fun fromHashMap(value: ImagesDtoMapper): String? {
        return Gson().toJson(value)
    }

}