package com.lpirro.tiledemo.customquicksettings

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.databinding.CustomTilesBinding


const val WIFI_ON = "wifi"

internal class TilesAdapater : RecyclerView.Adapter<TilesViewHolder>() {

    private val listTiles = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TilesViewHolder(
            CustomTilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TilesViewHolder, position: Int) {
        holder.imageView.setImageResource(listTiles[position])

        holder.imageView.setOnClickListener {
//            when(listTiles[position].name) {
//                WIFI_ON -> {
                    val wifiManager =  holder.imageView.context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                    wifiManager?.isWifiEnabled = true
//                }
//            }
        }
    }

    override fun getItemCount() = listTiles.size

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun setData(newList: List<Int>) {
        val diffUtils = TilesDiffUtils(listTiles, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        listTiles.clear()
        listTiles.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

data class TilesModel(val name: String, val drawable: Int)