package com.abdelmageed.chefatask.data.modules.local.locale

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import kotlinx.coroutines.flow.Flow

@Dao
interface MarvelDao {

    @Query("SELECT * FROM marvel WHERE modelId = :id")
    fun getDataById(id: Int?): MarvelModel?

    @Query("SELECT * FROM marvel")
    fun getAllMarvelImages(): Flow<List<MarvelModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCurrencies(
        marvelModel: MarvelModel? = null
    )

    @Query("UPDATE marvel SET imageDto =:imagesDtoMapper WHERE id = :id")
    suspend fun updateItem(id: Int?,imagesDtoMapper: ImagesDtoMapper)

}