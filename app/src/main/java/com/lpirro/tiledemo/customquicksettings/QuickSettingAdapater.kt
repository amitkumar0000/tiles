package com.lpirro.tiledemo.customquicksettings

import android.content.Context
import android.graphics.drawable.Icon
import android.net.wifi.WifiManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.databinding.CustomBrightnessLayoutBinding
import com.lpirro.tiledemo.databinding.CustomNotificationLayoutBinding
import com.lpirro.tiledemo.databinding.CustomTilesBinding
import java.lang.IllegalArgumentException


const val WIFI_ON = "wifi"
const val TILES = 0
const val BRIGHTNESS = 1
const val NOTIFICATIOn = 2

internal class TilesAdapater : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listTiles = arrayListOf<QuickSettingModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TILES -> {
                val binding = CustomTilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return QuickSettingViewHolder(binding)
            }
            BRIGHTNESS -> {
                return BrightnessViewHolder(CustomBrightnessLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
                bindTilesModel(holder = holder as QuickSettingViewHolder, listTiles[position] as QuickSettingModel.TilesModel)
            }
            is QuickSettingModel.NotificationModel -> {
                bindNotificationModel(holder = holder as NotificationViewHolder, listTiles[position] as QuickSettingModel.NotificationModel)
            }
            is QuickSettingModel.BrightnessModel -> {
                bindBrightnessModel(holder as BrightnessViewHolder, position)
            }
        }


    }

    private fun bindBrightnessModel(holder: BrightnessViewHolder, position: Int) {
        holder.brighnessSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               Log.d("STATUS", "Brighness increase by $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("STATUS", " onStartTrackingTouch $seekBar")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("STATUS", " onStopTrackingTouch")
            }

        })
    }

    private fun bindTilesModel(holder: QuickSettingViewHolder, tileModel: QuickSettingModel.TilesModel) {

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
        if(notificationModel.smallIcon == 0) {
            notificationModel.icon?.let {
                holder.smallIcon.setImageIcon(it)
            }
        } else {
            holder.smallIcon.setImageResource(notificationModel.smallIcon)
        }
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
            QuickSettingModel.BrightnessModel -> BRIGHTNESS
        }
    }

    fun setData(newList: List<QuickSettingModel>) {
        val diffUtils = QuickSettingDiffUtils(listTiles, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
//        listTiles.clear()
        listTiles.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteItem(position: Int) {
        listTiles.removeAt(position)
        notifyItemRemoved(position)
    }


}

sealed class QuickSettingModel {
    data class TilesModel(val name: String, val drawable: Int): QuickSettingModel()
    object BrightnessModel: QuickSettingModel()
    data class NotificationModel(val title: String, val content: String, val smallIcon: Int = 0, val icon: Icon? = null): QuickSettingModel()
}

