package com.abdelmageed.chefatask.presentation.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.BaseErrorResponse
import com.abdelmageed.chefatask.data.modules.remote.dto.MarvelComicsResponse
import com.abdelmageed.chefatask.extension.downloadImage
import com.abdelmageed.chefatask.extension.hideKeyboard
import com.abdelmageed.chefatask.extension.isOnline
import com.abdelmageed.chefatask.extension.showToast
import com.abdelmageed.chefatask.extension.toDomain
import com.abdelmageed.chefatask.R
import com.abdelmageed.chefatask.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var containsValue: Boolean = false
    private lateinit var adapter: MarvelComicsImagesAdapter
    private var marvelList: MutableList<ImagesDtoMapper?> = mutableListOf()
    private var marvelListInDb: MutableList<ImagesDtoMapper?> = mutableListOf()
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (requireActivity().isOnline())
            viewModel.getMarvelComics()

        viewModel.getAllMarvelImagesFromDb()
        observe()
        binding.apply {

            floatingAddImage.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToImageDetails(
                        null
                    )
                )
            }

            etSearch.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    val marvels = viewModel.search(it.toString(), marvelList)
                    if (::adapter.isInitialized) {
                        adapter.filter(marvels)
                    }
                } else {
                    requireActivity().hideKeyboard(binding.root)
                    adapter.filter(marvelList)
                }
            }
            swipeRefresh.isRefreshing = true
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = true
                if (::adapter.isInitialized) {
                    adapter.filter(mutableListOf())
                }
                if (requireActivity().isOnline())
                    viewModel.getMarvelComics()
            }
        }
    }

    private fun observe() {
        viewModel.state.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { state -> handleStateChange(state) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleStateChange(state: HomeFragmentState) {
        when (state) {
            is HomeFragmentState.ShowToast -> requireContext().showToast(state.message)
            is HomeFragmentState.IsLoading -> {}
            is HomeFragmentState.SuccessGetMarvelComics -> handleSuccessGetImage(state.marvelComicsResponse)
            is HomeFragmentState.ErrorGetMarvelComics -> handleErrorGetImage(state.errorResponse)
            is HomeFragmentState.GetAllImagesFromDB -> handleGetImagesFromDB(state.list)
            is HomeFragmentState.IsValueExistInDb -> checkIsValueExistInDb(state.isExist)
            is HomeFragmentState.Init -> Unit
        }
    }

    private fun checkIsValueExistInDb(exist: Boolean) {
        containsValue = exist
    }

    private fun handleGetImagesFromDB(list: MutableList<ImagesDtoMapper?>) {
        binding.swipeRefresh.isRefreshing = false
        marvelList = list
        marvelListInDb = list.distinctBy { it?.id }.toMutableList()
        if (!requireActivity().isOnline()) {

            if (marvelListInDb.isNotEmpty()) {

                adapter = MarvelComicsImagesAdapter(marvelListInDb, {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToImageDetails(
                            it
                        )
                    )
                }, {
                    AlertDialog.Builder(requireActivity())
                        .setMessage(getString(R.string.downloadImage))
                        .setPositiveButton(
                            getString(R.string.yes)
                        ) { dialog, _ ->
                            dialog.dismiss()
                            requireActivity().downloadImage(it)
                        }.setNegativeButton(
                            getString(R.string.no)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                })

                binding.apply {
                    rvMarvels.adapter = adapter
                    rvMarvels.layoutManager =
                        LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                    rvMarvels.isNestedScrollingEnabled = false
                    rvMarvels.setItemViewCacheSize(200)

                }
            } else {
                requireActivity().showToast("Cannot fetch data from database")
            }
        }
    }

    private fun handleErrorGetImage(errorResponse: BaseErrorResponse) {
        errorResponse.message?.let { requireActivity().showToast(it) }
    }

    private fun handleSuccessGetImage(marvelComicsResponse: MarvelComicsResponse) {
        binding.swipeRefresh.isRefreshing = false
        marvelComicsResponse.data?.results?.let { data ->
            if (data.isNotEmpty()) {
                val map = data.map { it!!.toDomain() }
                marvelList = map.toMutableList()
                marvelList.map { imageDao ->
                    imageDao?.id?.let { it1 -> viewModel.getItemFromDb(it1) } ?: false
                    if (!containsValue) {
                        val marvelModel = MarvelModel()
                        marvelModel.modelId = imageDao?.id
                        marvelModel.imageDtoModel = imageDao
                        viewModel.insertCurrenciesInDB(
                            marvelModel
                        )
                    }
                }
                val finalList = if (marvelListInDb.isNotEmpty()) {
                    marvelList.flatMap { marvelListInDb }.toMutableList()
                } else {
                    marvelList
                }
                val flatMap = finalList.distinctBy { it?.id }.toMutableList()
                adapter = MarvelComicsImagesAdapter(flatMap, {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToImageDetails(
                            it
                        )
                    )
                }, {
                    AlertDialog.Builder(requireActivity())
                        .setMessage(getString(R.string.downloadImage))
                        .setPositiveButton(
                            getString(R.string.yes)
                        ) { dialog, _ ->
                            dialog.dismiss()
                            requireActivity().downloadImage(it)
                        }.setNegativeButton(
                            getString(R.string.no)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                })

                binding.apply {
                    rvMarvels.adapter = adapter
                    rvMarvels.layoutManager =
                        LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                    rvMarvels.isNestedScrollingEnabled = false
                    rvMarvels.setItemViewCacheSize(200)

                }
            }
        }
    }
}