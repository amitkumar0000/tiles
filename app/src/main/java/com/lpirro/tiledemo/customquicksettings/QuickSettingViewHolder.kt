package com.lpirro.tiledemo.customquicksettings

import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.databinding.CustomTilesBinding

internal class QuickSettingViewHolder(binding: CustomTilesBinding ): RecyclerView.ViewHolder(binding.root) {
    val imageView = binding.tileImageView
    val tileLayout = binding.tileLayout
    val tileText = binding.tileText
}