package com.abdelmageed.chefatask.domain.home

import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.BaseErrorResponse
import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import com.abdelmageed.chefatask.domain.base.BaseResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMarvelComicsUseCase @Inject constructor(private val marvelComicsRepository: MarvelComicsRepository) {
    suspend fun getMarvelComics(): Flow<BaseResult<MarvelComicsResponse, BaseErrorResponse>> =
        marvelComicsRepository.getMarvelComics()

    suspend fun insertMarvel(
        marvelModel: MarvelModel
    ) {
        marvelComicsRepository.insertMarvelInDb(marvelModel)
    }

    suspend fun getAllMarvelImages(): Flow<List<MarvelModel>> {
        return marvelComicsRepository.getAllImages()
    }

    fun getItem(id: Int): MarvelModel? {
        return marvelComicsRepository.getItem(id)
    }
}