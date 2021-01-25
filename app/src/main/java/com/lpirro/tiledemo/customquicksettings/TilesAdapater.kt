package com.lpirro.tiledemo.customquicksettings

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.databinding.CustomNotificationLayoutBinding
import com.lpirro.tiledemo.databinding.CustomTilesBinding
import java.lang.IllegalArgumentException


const val WIFI_ON = "wifi"
const val TILES = 0
const val NOTIFICATIOn = 1

internal class TilesAdapater : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listTiles = arrayListOf<QuickSettingModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TILES -> {
                val binding = CustomTilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TilesViewHolder(binding)
            }
            NOTIFICATIOn -> {
                val binding = CustomNotificationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return NotificationViewHolder(binding)
            }
            else -> {
                throw IllegalArgumentException("Non supported viewType: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(listTiles[position]) {
            is QuickSettingModel.TilesModel -> {
                bindTilesModel(holder = holder as TilesViewHolder, listTiles[position] as QuickSettingModel.TilesModel)
            }
            is QuickSettingModel.NotificationModel -> {
                bindNotificationModel(holder = holder as NotificationViewHolder, listTiles[position] as QuickSettingModel.NotificationModel)
            }
        }


    }

    private fun bindTilesModel(holder: TilesViewHolder, tileModel: QuickSettingModel.TilesModel) {

        holder.imageView.setImageResource(tileModel.drawable)

        holder.imageView.setOnClickListener {
//            when(listTiles[position].name) {
//                WIFI_ON -> {
            val wifiManager =  holder.imageView.context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wifiManager?.isWifiEnabled = true
//                }
//            }
        }
    }

    private fun bindNotificationModel(holder: NotificationViewHolder, notificationModel: QuickSettingModel.NotificationModel) {
        holder.smallIcon.setImageResource(notificationModel.smallIcon)
        holder.title.text = notificationModel.title
        holder.content.text = notificationModel.content
    }

    override fun getItemCount() = listTiles.size

    override fun getItemViewType(position: Int): Int {
        return getViewType(position)
    }

    public fun getViewType(position: Int): Int {
        return when (listTiles[position]) {
            is QuickSettingModel.TilesModel -> TILES
            is QuickSettingModel.NotificationModel -> NOTIFICATIOn
        }
    }

    fun setData(newList: List<QuickSettingModel>) {
        val diffUtils = TilesDiffUtils(listTiles, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        listTiles.clear()
        listTiles.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

sealed class QuickSettingModel {
    data class TilesModel(val name: String, val drawable: Int): QuickSettingModel()
    data class NotificationModel(val title: String, val content: String, val smallIcon: Int): QuickSettingModel()
}

