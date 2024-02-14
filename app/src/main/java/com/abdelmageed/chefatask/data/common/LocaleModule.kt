package com.abdelmageed.chefatask.data.common

import android.content.Context
import androidx.room.Room
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelDao
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocaleModule {
    @Provides
    @Singleton
    fun provideImagesDatabase(@ApplicationContext context: Context): MarvelDatabase {
        return Room.databaseBuilder(
            context,
            MarvelDatabase::class.java,
            MarvelDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(db: MarvelDatabase): MarvelDao {
        return db.marvelDao()
    }
}