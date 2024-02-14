package com.abdelmageed.chefatask.data.modules.remote.api

import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("public/comics?")
    suspend fun getMarvelComics(
        @Query("ts") timeStamp: Long,
        @Query("apikey") apikey: String,
        @Query("hash") hash: String
    ): Response<MarvelComicsResponse>
}