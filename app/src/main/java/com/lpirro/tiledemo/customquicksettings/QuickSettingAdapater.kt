package com.lpirro.tiledemo.customquicksettings

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Icon
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.Utils
import com.lpirro.tiledemo.customquicksettings.bluetooth.CustomBluetooth
import com.lpirro.tiledemo.databinding.CustomBrightnessLayoutBinding
import com.lpirro.tiledemo.databinding.CustomNotificationLayoutBinding
import com.lpirro.tiledemo.databinding.CustomTilesBinding


const val WIFI_ON = "wifi"
const val TILES = 0
const val BRIGHTNESS = 1
const val NOTIFICATIOn = 2

internal class TilesAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listTiles = arrayListOf<QuickSettingModel>()
    private val bluetooth = CustomBluetooth(context)

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
        holder.brighnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("STATUS", "Brighness increase by $progress")

                val cResolver: ContentResolver =  holder.brighnessSeekBar.context.applicationContext.getContentResolver()
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, progress%255)
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
        holder.tileText.text = tileModel.name

//        when(tileModel.name) {
//             BLUETOOTH -> {
//                 configBluetoothTiles(holder, tileModel)
//             }
//        }
        preapareTiles(holder, tileModel)
    }

    fun onCleared() {
        bluetooth.unregister()
    }

    private fun preapareTiles(holder: QuickSettingViewHolder, tileModel: QuickSettingModel.TilesModel) {
        holder.tileLayout.setOnClickListener {
            tileModel.state = !tileModel.state
            holder.tileLayout.isClickable = false
            configure(holder, tileModel)
        }
    }

    private fun prepareTileState(holder: QuickSettingViewHolder, enable: Boolean) {
        if(enable) {
            holder.tileLayout.setBackgroundResource(R.drawable.circle_blue)
            Utils.setTint(holder.imageView, android.R.color.white)
        }else {
            holder.tileLayout.setBackgroundResource(R.drawable.circle_gray)
            Utils.setTint(holder.imageView, R.color.tileimagecolor)
        }
        holder.tileLayout.isClickable = true

    }

    private fun configure(holder: QuickSettingViewHolder, tileModel: QuickSettingModel.TilesModel) {
        when(tileModel.name) {
            BLUETOOTH -> {
                bluetooth.configure(tileModel.state) {
                    enable ->
                    prepareTileState(holder, enable)
                }
            }
            WIFI -> {

            }

            AIRPLANE -> {

            }
            FLASHLIGHT -> {

            }
            ROTATION -> {

            }
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

    fun getViewType(position: Int): Int {
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
    data class TilesModel(val name: String, val drawable: Int, var state: Boolean = false): QuickSettingModel()
    object BrightnessModel: QuickSettingModel()
    data class NotificationModel(val title: String, val content: String, val smallIcon: Int = 0, val icon: Icon? = null): QuickSettingModel()
}
