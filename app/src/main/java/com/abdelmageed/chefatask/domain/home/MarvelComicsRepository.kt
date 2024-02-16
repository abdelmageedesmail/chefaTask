package com.abdelmageed.chefatask.domain.home

import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.BaseErrorResponse
import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import com.abdelmageed.chefatask.domain.base.BaseResult
import dagger.Module
import kotlinx.coroutines.flow.Flow

interface MarvelComicsRepository {

    suspend fun getMarvelComics(): Flow<BaseResult<MarvelComicsResponse, BaseErrorResponse>>
    suspend fun insertMarvelInDb(
        marvelModel: MarvelModel
    )

    suspend fun getAllImages(): Flow<List<MarvelModel>>

    fun getItem(id: Int): MarvelModel?

    suspend fun updateImage(id: Int?, imagesDtoMapper: ImagesDtoMapper)
}