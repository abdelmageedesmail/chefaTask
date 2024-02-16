package com.abdelmageed.chefatask.extension

import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.ResultsItem


fun ResultsItem.toDomain() = ImagesDtoMapper(
    id = this.id,
    imageUrl = "${thumbnail?.path}.${thumbnail?.extension}",
    title = title ?: "",
    date = modified ?: "",
    bitmap = null,
    bufferArray = null
)