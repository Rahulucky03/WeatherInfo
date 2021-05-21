package com.weather.info.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.weather.info.R

class BindingAdapters {

    @BindingAdapter("loadSrc")
    fun ImageView.loadImage(loadSrc: String?) {
        Glide.with(context)
            .load(loadSrc)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(this)
    }

}