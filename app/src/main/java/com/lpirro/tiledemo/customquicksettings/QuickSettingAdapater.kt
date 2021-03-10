package com.lpirro.tiledemo.customquicksettings

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.Utils
import com.lpirro.tiledemo.customquicksettings.autorotate.AutoRotateConfig
import com.lpirro.tiledemo.customquicksettings.bluetooth.CustomBluetooth
import com.lpirro.tiledemo.customquicksettings.flashlight.FlashLightConfig
import com.lpirro.tiledemo.customquicksettings.gps.GpsConfig
import com.lpirro.tiledemo.customquicksettings.mobiledata.MobileDataConfig
import com.lpirro.tiledemo.customquicksettings.wifi.WifiConfig
import com.lpirro.tiledemo.databinding.CustomNotificationLayoutBinding
import com.lpirro.tiledemo.databinding.CustomTilesBinding
import com.lpirro.tiledemo.sharing.*


const val WIFI_ON = "wifi"
const val TILES = 0
const val BRIGHTNESS = 1
const val NOTIFICATIOn = 2

internal class TilesAdapter(val context: Context, val windowManager: WindowManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listTiles = arrayListOf<QuickSettingModel>()
    private val bluetooth = CustomBluetooth(context)
    private val wifiConfig = WifiConfig(context)
    private val autoRotateConfig = AutoRotateConfig(context)
    private val mobileDataConfig = MobileDataConfig(context)
    private val gpsConfig = GpsConfig(context, windowManager)
    private val nfcConfig = NfcConfig(context)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val flashLightConfig = FlashLightConfig(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TILES -> {
                val binding = CustomTilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return QuickSettingViewHolder(binding)
            }
//            BRIGHTNESS -> {
//                return BrightnessViewHolder(CustomBrightnessLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//            }
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
//            is QuickSettingModel.NotificationModel -> {
//                bindNotificationModel(holder = holder as NotificationViewHolder, listTiles[position] as QuickSettingModel.NotificationModel)
//            }
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
            if(tileModel.isReadOnly)
                return@setOnClickListener
            tileModel.state = !tileModel.state
            holder.tileLayout.isClickable = false
            configure(holder, tileModel)
        }
        initConfig(holder, tileModel)
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

    private fun initConfig(holder: QuickSettingViewHolder, tileModel: QuickSettingModel.TilesModel) {
        when(tileModel.name) {
            Bluetooth -> {
                bluetooth.initConfig { enable ->
                    prepareTileState(holder, enable)
                }
            }
            WLAN -> {
                wifiConfig.initConfig { enable ->
                    prepareTileState(holder, enable)
                }
            }
            GPS -> {
                gpsConfig.initConfig { enable ->
                    prepareTileState(holder, enable)
                }
            }
            NFC -> {
                nfcConfig.initConfig { enable ->
                    prepareTileState(holder, enable)
                }
            }
            MOBILEDATA -> {
                mobileDataConfig.initConfig { enable ->
                    prepareTileState(holder, enable)
                }
            }
            else -> { }
        }
    }

    private fun configure(holder: QuickSettingViewHolder, tileModel: QuickSettingModel.TilesModel) {
        when(tileModel.name) {
            Bluetooth -> {
                bluetooth.configure(tileModel.state) {
                    enable ->
                    prepareTileState(holder, enable)
                }
            }
            WLAN -> {
                wifiConfig.configWifi(tileModel.state)
                prepareTileState(holder, tileModel.state)
            }

            MOBILEDATA -> {
                mobileDataConfig.setMobileDataState(tileModel.state) {
                    enable -> prepareTileState(holder, enable)
                }
            }
            Flashlight -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        flashLightConfig.configFlashLight(tileModel.state) {
                            enable ->
                            prepareTileState(holder, enable)
                        }
                    }
                }
            }
//            ROTATION -> {
//                autoRotateConfig.config(tileModel.state)
//                prepareTileState(holder, tileModel.state)
//            }
            GPS -> {
                gpsConfig.changeGpsEnabled(tileModel.state) {
                    enable -> prepareTileState(holder, enable)
                }
            }
            NFC -> {
                nfcConfig.changeNfcEnabled(tileModel.state) {
                    enable -> prepareTileState(holder, enable)
                }
            }
        }
    }


//    private fun bindNotificationModel(holder: NotificationViewHolder, notificationModel: QuickSettingModel.NotificationModel) {
//        if(notificationModel.smallIcon == 0) {
//            notificationModel.icon?.let {
//                holder.smallIcon.setImageIcon(it)
//            }
//        } else {
//            holder.smallIcon.setImageResource(notificationModel.smallIcon)
//        }
//        holder.title.text = notificationModel.title
//        holder.content.text = notificationModel.content
//    }

    override fun getItemCount() = listTiles.size

    override fun getItemViewType(position: Int): Int {
        return getViewType(position)
    }

    fun getViewType(position: Int): Int {
        return when (listTiles[position]) {
            is QuickSettingModel.TilesModel -> TILES
            QuickSettingModel.BrightnessModel -> BRIGHTNESS
        }
    }

    fun setData(newList: List<QuickSettingModel>) {
//        val diffUtils = QuickSettingDiffUtils(listTiles, newList)
//        val diffResult = DiffUtil.calculateDiff(diffUtils)
        listTiles.clear()
        listTiles.addAll(newList)
        notifyDataSetChanged()
//        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteItem(position: Int) {
        listTiles.removeAt(position)
        notifyItemRemoved(position)
    }


}

sealed class QuickSettingModel {
    data class TilesModel(val name: String, val drawable: Int, var state: Boolean = false, var isReadOnly: Boolean = false): QuickSettingModel()
    object BrightnessModel: QuickSettingModel()
//    data class NotificationModel(val title: String, val content: String, val smallIcon: Int = 0, val icon: Icon? = null): QuickSettingModel()
}
