package com.abdelmageed.chefatask.presentation.home

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.BaseErrorResponse
import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import com.abdelmageed.chefatask.domain.base.BaseResult
import com.abdelmageed.chefatask.domain.home.GetMarvelComicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val marvelComicsUseCase: GetMarvelComicsUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<HomeFragmentState>(HomeFragmentState.Init)

    val state: StateFlow<HomeFragmentState> get() = _state

    private fun getMarvelComics(marvelComicsResponse: MarvelComicsResponse) {
        _state.value = HomeFragmentState.SuccessGetMarvelComics(marvelComicsResponse)
    }

    private fun errorGetMarvelComics(errorResponse: BaseErrorResponse) {
        _state.value = HomeFragmentState.ErrorGetMarvelComics(errorResponse)
    }

    private fun showToast(message: String) {
        _state.value = HomeFragmentState.ShowToast(message)
    }

    private fun shouldShowLoading(isLoading: Boolean) {
        _state.value = HomeFragmentState.IsLoading(isLoading)
    }

    private fun isExistInDb(isExistInDb: Boolean) {
        _state.value = HomeFragmentState.IsValueExistInDb(isExistInDb)
    }

    fun getMarvelComics() {
        viewModelScope.launch {
            shouldShowLoading(true)
            marvelComicsUseCase.getMarvelComics().catch {
                shouldShowLoading(false)
                showToast(it.message.toString())
            }.collect {
                shouldShowLoading(false)
                when (it) {
                    is BaseResult.Success -> getMarvelComics(it.data)
                    is BaseResult.Error -> errorGetMarvelComics(it.rawResponse)
                }
            }
        }
    }

    fun search(
        keyword: String,
        list: MutableList<ImagesDtoMapper?>
    ): MutableList<ImagesDtoMapper?> {
        return list.filter { it?.title.toString().contains(keyword, ignoreCase = true) }
            .toMutableList()
    }


    private fun getAllCurrenciesFromDb(list: MutableList<ImagesDtoMapper?>) {
        _state.value = HomeFragmentState.GetAllImagesFromDB(list)
    }

    fun insertCurrenciesInDB(marvelModel: MarvelModel) {
        viewModelScope.launch {
            Log.e("marvelTitle", "${marvelModel.imageDtoModel?.title}")
            marvelComicsUseCase.insertMarvel(marvelModel)
        }
    }

    fun getItemFromDb(id: Int) {
        var isExist = false
        viewModelScope.launch(Dispatchers.IO) {
            val item = marvelComicsUseCase.getItem(id)
            if (item != null) {
                isExist = true
            }
            isExistInDb(isExist)
        }
    }

    fun getAllMarvelImagesFromDb() {
        viewModelScope.launch {
            marvelComicsUseCase.getAllMarvelImages().catch {
                Log.e("listSize", "${it.message.toString()}")
                showToast(it.message.toString())
            }.collect {
                val list = mutableListOf<ImagesDtoMapper?>()

                it.map { marvelModel ->
                    Log.e("byteArray2", "${marvelModel.byte}")
                    marvelModel.imageDtoModel?.let { imageDto ->
                        list.add(imageDto)
                    }
                }
                getAllCurrenciesFromDb(list)
            }
        }
    }
}

sealed class HomeFragmentState {
    object Init : HomeFragmentState()
    data class SuccessGetMarvelComics(val marvelComicsResponse: MarvelComicsResponse) :
        HomeFragmentState()

    data class ErrorGetMarvelComics(val errorResponse: BaseErrorResponse) : HomeFragmentState()
    data class ShowToast(val message: String) : HomeFragmentState()
    data class IsLoading(val isLoading: Boolean) : HomeFragmentState()
    data class IsValueExistInDb(val isExist: Boolean) : HomeFragmentState()
    class GetAllImagesFromDB(val list: MutableList<ImagesDtoMapper?>) : HomeFragmentState()
}