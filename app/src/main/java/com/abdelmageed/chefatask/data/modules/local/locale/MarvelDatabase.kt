package com.abdelmageed.chefatask.data.modules.local.locale

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MarvelModel::class], version = 1, exportSchema = false)
@TypeConverters(ArrayListConverter::class, ObjectTypeConverter::class)
abstract class MarvelDatabase : RoomDatabase() {

    abstract fun marvelDao(): MarvelDao

    companion object {
        const val DATABASE_NAME = "marvel_db"
    }

}