package com.abdelmageed.chefatask.data.common

import com.abdelmageed.chefatask.data.modules.local.locale.MarvelDao
import com.abdelmageed.chefatask.data.modules.remote.api.ApiInterface
import com.abdelmageed.chefatask.data.modules.repository.MarvelComicsRepositoryImpl
import com.abdelmageed.chefatask.domain.home.MarvelComicsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {
    @Singleton
    @Provides
    fun provideMarvelComicsRepo(apiInterface: ApiInterface,dao: MarvelDao): MarvelComicsRepository =
        MarvelComicsRepositoryImpl(apiInterface,dao)
}