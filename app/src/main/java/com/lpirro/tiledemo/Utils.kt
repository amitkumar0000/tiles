package com.lpirro.tiledemo

import android.R
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

object Utils {
    fun setTint(view: ImageView, color: Int) {
        DrawableCompat.setTint(
                DrawableCompat.wrap(view.drawable),
                ContextCompat.getColor(view.context, color)
        )
    }
}