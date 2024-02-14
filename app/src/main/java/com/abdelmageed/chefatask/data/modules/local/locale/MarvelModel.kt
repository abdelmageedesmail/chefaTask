package com.abdelmageed.chefatask.data.modules.local.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper

@androidx.annotation.Keep
@Entity(tableName = "marvel")
data class MarvelModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "modelId")
    var modelId: Int? = null,

    @TypeConverters(ObjectTypeConverter::class)
    @ColumnInfo(name = "imageDto")
    var selectedCurrencies: ImagesDtoMapper? = null
)
