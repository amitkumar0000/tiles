package com.lpirro.tiledemo.customquicksettings

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.databinding.CustomTilesBinding

internal class TilesAdapater : RecyclerView.Adapter<TilesViewHolder>() {

    private val listTiles = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TilesViewHolder(
            CustomTilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TilesViewHolder, position: Int) {
        holder.imageView.setImageResource(listTiles[position])
    }

    override fun getItemCount() = listTiles.size

    fun setData(newList: List<Int>) {
        val diffUtils = TilesDiffUtils(listTiles, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        listTiles.clear()
        listTiles.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}