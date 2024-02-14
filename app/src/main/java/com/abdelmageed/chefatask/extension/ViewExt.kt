package com.abdelmageed.chefatask.extension

import android.view.View
import android.widget.ImageView
import com.abdelmageed.chefatask.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy



fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ImageView.applyImage(imageUrl: String) {
    Glide.with(this.context).load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(false)
        .error(R.drawable.ic_marvel).into(this)
}