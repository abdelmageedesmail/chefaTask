package com.abdelmageed.chefatask.data.modules.repository

import com.abdelmageed.chefatask.data.modules.local.locale.MarvelDao
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.abdelmageed.chefatask.data.modules.remote.api.ApiInterface
import com.abdelmageed.chefatask.data.modules.remote.dto.BaseErrorResponse
import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import com.abdelmageed.chefatask.domain.base.BaseResult
import com.abdelmageed.chefatask.domain.home.MarvelComicsRepository
import com.abdelmageed.chefatask.extension.getHash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MarvelComicsRepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface,
    private val marvelDao: MarvelDao
) :
    MarvelComicsRepository {

    private val currentTimestamp = System.currentTimeMillis()

    init {
        System.loadLibrary("native-lib")
    }

    private external fun getPublicKey(): String

    private external fun getPrivateKey(): String

    override suspend fun getMarvelComics(): Flow<BaseResult<MarvelComicsResponse, BaseErrorResponse>> =
        flow {
            val stringHash = "$currentTimestamp${getPrivateKey()}${getPublicKey()}"
            val response =
                apiInterface.getMarvelComics(currentTimestamp, getPublicKey(), stringHash.getHash())
            if (response.isSuccessful) {
                response.body()?.let { imageResponse ->
                    emit(BaseResult.Success(imageResponse))
                }
            } else {
                if (response.code() == 500) {
                    val baseErrorResponse = BaseErrorResponse(
                        "Server Error",
                        "Server error please try again later",
                        500
                    )
                    emit(BaseResult.Error(baseErrorResponse))
                } else {
                    val type =
                        object : TypeToken<BaseErrorResponse>() {}.type
                    val err: BaseErrorResponse =
                        Gson().fromJson(response.errorBody()!!.charStream(), type)
                    emit(BaseResult.Error(err))
                }
            }
        }

    override suspend fun insertMarvelInDb(
        marvelModel: MarvelModel
    ) {
        marvelDao.insertAllCurrencies(marvelModel)
    }

    override suspend fun getAllImages(): Flow<List<MarvelModel>> {
        return marvelDao.getAllMarvelImages()
    }

    override fun getItem(id: Int): MarvelModel? {
        return marvelDao.getDataById(id)
    }


}