package com.abdelmageed.chefatask.presentation.imageDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.BaseErrorResponse
import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import com.abdelmageed.chefatask.domain.base.BaseResult
import com.abdelmageed.chefatask.domain.home.GetMarvelComicsUseCase
import com.abdelmageed.chefatask.presentation.home.HomeFragmentState
import com.tinify.Options
import com.tinify.Source
import com.tinify.Tinify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val marvelComicsUseCase: GetMarvelComicsUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<ImageDetailsState>(ImageDetailsState.Init)

    val state: StateFlow<ImageDetailsState> get() = _state
    private fun itemDetails(model: MarvelModel) {
        _state.value = ImageDetailsState.GetImageModel(model)
    }

    private fun getAllCurrenciesFromDb(list: MutableList<ImagesDtoMapper?>) {
        _state.value = ImageDetailsState.GetAllImagesFromDB(list)
    }


    fun getItemDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = marvelComicsUseCase.getItem(id)
            if (item != null) {
                itemDetails(item)
            }
        }
    }

    fun getAllMarvelImagesFromDb() {
        viewModelScope.launch {
            marvelComicsUseCase.getAllMarvelImages().catch {
            }.collect {
                val list = mutableListOf<ImagesDtoMapper?>()
                it.map { marvelModel ->
                    marvelModel.imageDtoModel?.let { imageDto ->
                        list.add(imageDto)
                    }
                }
                getAllCurrenciesFromDb(list)
            }
        }
    }

    fun insertImageInDB(marvelModel: MarvelModel) {
        viewModelScope.launch {
            marvelComicsUseCase.insertMarvel(marvelModel)
        }
    }

    fun updateItem(id: Int?, imagesDtoMapper: ImagesDtoMapper) {
        viewModelScope.launch {
            marvelComicsUseCase.updateImage(id, imagesDtoMapper)
        }
    }
}

sealed class ImageDetailsState {
    object Init : ImageDetailsState()
    data class GetImageModel(val model: MarvelModel) : ImageDetailsState()
    data class GetAllImagesFromDB(val list: MutableList<ImagesDtoMapper?>) : ImageDetailsState()
}
