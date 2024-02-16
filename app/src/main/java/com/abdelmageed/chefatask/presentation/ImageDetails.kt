package com.abdelmageed.chefatask.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abdelmageed.chefatask.R
import com.abdelmageed.chefatask.data.modules.local.locale.MarvelModel
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.databinding.FragmentImageDetailsBinding
import com.abdelmageed.chefatask.extension.applyImage
import com.abdelmageed.chefatask.extension.resizeImage
import com.abdelmageed.chefatask.presentation.imageDetails.ImageDetailsState
import com.abdelmageed.chefatask.presentation.imageDetails.ImageViewModel
import com.abdelmageed.chefatask.utils.ImageColor.getDominantColor
import com.abdelmageed.chefatask.utils.ImageColor.isDarkColor
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tinify.Options
import com.tinify.Source
import com.tinify.Tinify
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date


@AndroidEntryPoint
class ImageDetails : Fragment() {

    private var byteArray: ByteArray? = null
    private var dbSize: Int = 0
    private var bitmap: Bitmap? = null
    private var imageDtoModel: ImagesDtoMapper? = null
    private var itemId: Int = -1
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private val args: ImageDetailsArgs by navArgs()
    private val viewModel: ImageViewModel by viewModels()
    private lateinit var binding: FragmentImageDetailsBinding


    init {
        System.loadLibrary("native-lib")
    }

    private external fun getTinifyApiKey(): String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        viewModel.getAllMarvelImagesFromDb()
        binding.apply {
            if (args.resultsItem != null) {
                imageDtoModel = args.resultsItem
                imageDtoModel?.id?.let { viewModel.getItemDetails(it) }
                val image = args.resultsItem
                val imageUrl = image?.imageUrl?.replace(
                    "http",
                    "https"
                )
                imageUrl?.let { ivImage.applyImage(it) }
                if (image?.bufferArray?.isNotEmpty() == true) {
                    byteToBitmap(image.bufferArray)
                    imageWidth = byteToBitmap(image.bufferArray)?.width ?: 0
                    imageHeight = byteToBitmap(image.bufferArray)?.height ?: 0
                    etWidth.setText(imageWidth.toString())
                    etHeight.setText(imageHeight.toString())

                    val hexValue =
                        Integer.toHexString(
                            getDominantColor(byteToBitmap(image.bufferArray)).and(0xFFFFFF)
                        )

                    binding.root.setBackgroundColor(Color.parseColor("#$hexValue"))

                    if (isDarkColor(getDominantColor(byteToBitmap(image.bufferArray)))) {
                        etCaption.setTextColor(Color.WHITE)
                        etWidth.setTextColor(Color.WHITE)
                        etHeight.setTextColor(Color.WHITE)
                    }
                } else {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(imageUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                imageWidth = resource.width
                                imageHeight = resource.height
                                etWidth.setText(imageWidth.toString())
                                etHeight.setText(imageHeight.toString())
                                val hexValue =
                                    Integer.toHexString(
                                        getDominantColor(resource).and(0xFFFFFF)
                                    )
                                binding.root.setBackgroundColor(Color.parseColor("#$hexValue"))

                                if (isDarkColor(getDominantColor(resource))) {
                                    etCaption.setTextColor(Color.WHITE)
                                    etWidth.setTextColor(Color.WHITE)
                                    etHeight.setTextColor(Color.WHITE)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })

                }

                if (image?.title.toString().isNotEmpty())
                    etCaption.setText(image?.title.toString())
                else {
                    etCaption.setText(getString(R.string.noCaption))
                    nameLayout.requestFocus()
                    nameLayout.boxStrokeColor = Color.RED
                }

            } else {
                requestPermission()
            }

            ivBack.setOnLongClickListener {
                findNavController().popBackStack()
            }
            btnSubmit.setOnClickListener {
                if (etCaption.text.toString().isEmpty()) {
                    etCaption.error = getString(R.string.emptyField)
                } else if (etWidth.text.toString().isEmpty()) {
                    etWidth.error = getString(R.string.emptyField)
                } else if (etHeight.text.toString().isEmpty()) {
                    etHeight.error = getString(R.string.emptyField)
                } else {
                    if (imageDtoModel != null) {
                        viewModel.updateItem(
                            itemId,
                            imagesDtoMapper = ImagesDtoMapper(
                                id = imageDtoModel?.id,
                                imageUrl = imageDtoModel?.imageUrl.toString(),
                                title = binding.etCaption.text.toString(),
                                bufferArray = byteArray
                            )
                        )
                    } else {
                        val min = 10
                        val max = 20
                        val randomNum = (min..max).random()
                        viewModel.insertImageInDB(
                            MarvelModel(
                                dbSize, randomNum,
                                byte = byteArray,
                                ImagesDtoMapper(
                                    randomNum,
                                    "",
                                    binding.etCaption.text.toString(),
                                    Date().toString(),
                                    bufferArray = byteArray
                                )
                            )
                        )
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun resizeImage(): ByteArray? {
        var byteArray: ByteArray? = null
        if (args.resultsItem != null) {
            if (imageDtoModel?.bufferArray != null) {
                byteToBitmap(imageDtoModel?.bufferArray)?.let {
                    val media = it.resizeImage(
                        binding.etWidth.text.toString().toInt(),
                        binding.etHeight.text.toString().toInt(),
                        100
                    )
                    byteArray = media
                }
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        Tinify.setKey(getTinifyApiKey())
                        // Stuff that updates the UI
                        val source: Source =
                            Tinify.fromUrl(args.resultsItem!!.imageUrl)

                        val media = source.resize(
                            Options().with("method", "fit")
                                .with("width", binding.etWidth.text.toString().toInt())
                                .with("height", binding.etHeight.text.toString().toInt())
                        ).result().toBuffer()
                        byteArray = media

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        } else {
            bitmap?.let {
                val media = it.resizeImage(
                    binding.etWidth.text.toString().toInt(),
                    binding.etHeight.text.toString().toInt(),
                    70
                )
                byteArray = media
            }
        }
        return byteArray
    }

    private fun observe() {
        viewModel.state.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { state -> handleStateChange(state) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleStateChange(state: ImageDetailsState) {
        when (state) {
            is ImageDetailsState.GetImageModel -> getImageDetails(state.model)
            is ImageDetailsState.GetAllImagesFromDB -> dbSize = state.list.size
            is ImageDetailsState.Init -> Unit
        }
    }

    private fun getImageDetails(model: MarvelModel) {
        itemId = model.id
        imageDtoModel = model.imageDtoModel
    }

    private fun requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                pickUpImage()
            }
        }

    private fun pickUpImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            launchResult.launch(intent)
        } else {
            requestPermission()
        }
    }


    private val launchResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        Uri.parse(uri.toString())
                    )
                    byteArray =
                        requireActivity().contentResolver.openInputStream(uri)?.readBytes().let {
                            it
                        }
                    imageWidth = bitmap?.width!!
                    imageHeight = bitmap?.height!!
                    binding.etWidth.setText(imageWidth.toString())
                    binding.etHeight.setText(imageHeight.toString())

                    val hexValue =
                        Integer.toHexString(getDominantColor(bitmap).and(0xFFFFFF))

                    binding.root.setBackgroundColor(Color.parseColor("#$hexValue"))

                    if (isDarkColor(getDominantColor(bitmap))) {
                        binding.etCaption.setTextColor(Color.WHITE)
                        binding.etWidth.setTextColor(Color.WHITE)
                        binding.etHeight.setTextColor(Color.WHITE)
                    }
                    binding.ivImage.setImageBitmap(bitmap)
                }
            }
        }


    private fun byteToBitmap(b: ByteArray?): Bitmap? {
        return if (b == null || b.isEmpty()) null else BitmapFactory
            .decodeByteArray(b, 0, b.size)
    }
}