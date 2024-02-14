package com.abdelmageed.chefatask.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abdelmageed.chefatask.utils.ImageColor.getDominantColor
import com.abdelmageed.chefatask.utils.ImageColor.isDarkColor
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.abdelmageed.chefatask.R
import com.abdelmageed.chefatask.databinding.FragmentImageDetailsBinding
import com.tinify.Options
import com.tinify.Source
import com.tinify.Tinify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ImageDetails : Fragment() {

    private var compressedBitmap: Bitmap? = null
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private val args: ImageDetailsArgs by navArgs()
    private lateinit var binding: FragmentImageDetailsBinding

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
        binding.apply {
            val image = args.resultsItem
            val imageUrl = image?.imageUrl?.replace(
                "http",
                "https"
            )
//            ivImage.applyImage(imageUrl)

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
                            Integer.toHexString(getDominantColor(resource).and(0xFFFFFF) ?: 0)

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


            if (image?.title.toString().isNotEmpty())
                etCaption.setText(image?.title.toString())
            else {
                etCaption.setText(getString(R.string.noCaption))
                nameLayout.requestFocus()
                nameLayout.boxStrokeColor = Color.RED
            }
            ivBack.setOnLongClickListener {
                findNavController().popBackStack()
            }

            lifecycleScope.launch(Dispatchers.IO) {
                Tinify.setKey("HsCYgVJHV200XTbFRbQd5cSVT6FNyr45")
                try {
                    val source: Source =
                        Tinify.fromUrl("https://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available.jpg")

                    val mediaType = source.resize(
                        Options().with("method", "fit").with("width", 1000).with("height", 800)
                    ).result().toBuffer()

                    compressedBitmap = byteToBitmap(mediaType)
                    requireActivity().runOnUiThread(Runnable {
                        // Stuff that updates the UI
                        ivImage.setImageBitmap(compressedBitmap)
                    })

//                resize.result().

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun byteToBitmap(b: ByteArray?): Bitmap? {
        return if (b == null || b.size == 0) null else BitmapFactory
            .decodeByteArray(b, 0, b.size)
    }

}