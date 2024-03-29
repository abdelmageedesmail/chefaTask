package com.abdelmageed.chefatask.presentation.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdelmageed.chefatask.data.modules.model.ImagesDtoMapper
import com.abdelmageed.chefatask.data.modules.remote.dto.ResultsItem
import com.abdelmageed.chefatask.extension.applyImage
import com.abdelmageed.chefatask.R
import com.abdelmageed.chefatask.databinding.ItemMarvelBinding
import java.text.DateFormat


class MarvelComicsImagesAdapter(
    private var images: MutableList<ImagesDtoMapper?> = mutableListOf(),
    private val clickListener: (ImagesDtoMapper) -> Unit,
    private val downloadListener: (ImagesDtoMapper) -> Unit
) : RecyclerView.Adapter<MarvelComicsImagesAdapter.JobsViewHolder>() {
    private lateinit var binding: ItemMarvelBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        binding = ItemMarvelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JobsViewHolder(
            binding.root
        )
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        images[position]?.let { holder.bind(it) }
        holder.setIsRecyclable(false)

    }

    fun filter(imageList: MutableList<ImagesDtoMapper?>) {
        images = imageList
//        notifyItemRangeChanged(0, imageList.size)
        notifyDataSetChanged()
    }


    override fun getItemCount() = images.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class JobsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(image: ImagesDtoMapper) {
            binding.apply {
                if (image.title.toString().isNotEmpty())
                    tvTitle.text = image.title
                else
                    tvTitle.text = view.context.getString(R.string.noCaption)

                val df = DateFormat.getInstance().format(DateFormat.DAY_OF_WEEK_FIELD)

                tvDate.text = df.format(image.date)

                if (image.bufferArray != null) {
                    Log.e("ImageBitmap", "${image.bufferArray}")

                    ivMarvel.setImageBitmap(byteToBitmap(image.bufferArray))
                } else {
                    val imageUrl = image.imageUrl.replace(
                        "http",
                        "https"
                    )
                    Log.e("imageUrl", "${image.id}")
                    ivMarvel.applyImage(imageUrl)
                }

                ivMarvel.setOnClickListener {
                    clickListener(image)
                }

                ivMarvel.setOnLongClickListener {
                    val imageUrl = image.imageUrl.replace(
                        "http",
                        "https"
                    )
                    Log.e("imageUrl", "${image.id}")
                    downloadListener(image)
                    true
                }
            }
        }


        private fun byteToBitmap(b: ByteArray?): Bitmap? {
            return if (b == null || b.size == 0) null else BitmapFactory
                .decodeByteArray(b, 0, b.size)
        }
    }
}