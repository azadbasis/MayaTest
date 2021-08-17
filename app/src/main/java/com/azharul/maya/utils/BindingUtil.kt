package com.azharul.maya.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.azharul.maya.utils.Constants.Companion.IMAGE_BASE_URL
import com.bumptech.glide.Glide

@BindingAdapter("image")
fun loadImage(view: ImageView,url:String){
    var imagePath=IMAGE_BASE_URL+url
    Glide.with(view)
        .load(imagePath)
        .into(view)
}