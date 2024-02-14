package com.abdelmageed.chefatask.presentation.home

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
    private val downloadListener: (String) -> Unit
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
                val imageUrl = image.imageUrl.replace(
                    "http",
                    "https"
                )
                Log.e("imageUrl", "${image.id}")
                if (image.title.toString().isNotEmpty())
                    tvTitle.text = image.title
                else
                    tvTitle.text = view.context.getString(R.string.noCaption)

                val df = DateFormat.getInstance().format(DateFormat.DAY_OF_WEEK_FIELD)

                tvDate.text = df.format(image.date)

                if (image.bitmap != null) {
                    ivMarvel.setImageBitmap(image.bitmap)
                } else {
                    ivMarvel.applyImage(imageUrl)
                }

                ivMarvel.setOnClickListener {
                    clickListener(image)
                }

                ivMarvel.setOnLongClickListener {
                    downloadListener(imageUrl)
                    true // returning true instead of false, works for me
                }
            }
        }
    }
}