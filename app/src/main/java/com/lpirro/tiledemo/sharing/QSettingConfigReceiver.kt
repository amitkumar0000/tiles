package com.lpirro.tiledemo.sharing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.lpirro.tiledemo.ConfigSetting
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.RxBus
import com.lpirro.tiledemo.Utils.QSETTING_CONFIG
import com.lpirro.tiledemo.customquicksettings.QuickSettingModel


const val WIFI = "WIFI"
const val WIFI_READ_STATE = "WIFI_READ_STATE"
const val BLUETOOTH = "BLUETOOTH"
const val BLUETOOTH_READ_STATE = "BLUETOOTH_READ_STATE"
const val FLASHLIGHT = "FLASHLIGHT"
const val FLASHLIGHT_READ_STATE = "FLASHLIGHT_READ_STATE"
const val GPS = "GPS"
const val GPS_READ_STATE = "GPS_READ_STATE"
const val MOBILEDATA = "MOBILEDATA"
const val MOBILEDATA_READ_STATE = "MOBILEDATA_READ_STATE"
const val NFC = "NFC"
const val NFC_READ_STATE = "NFC_READ_STATE"

class QSettingConfigReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedpreferences by lazy {  context?.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE) }

        intent?.let {
            val list = ArrayList<QuickSettingModel.TilesModel>().apply {

                if(intent.getBooleanExtra(WIFI, false)) {
                    val isReadOnly = intent.getBooleanExtra(WIFI_READ_STATE, false)
                    add(QuickSettingModel.TilesModel(WIFI, R.drawable.ic_wifi, isReadOnly = isReadOnly))
                }
                if(intent.getBooleanExtra(BLUETOOTH, false)) {
                    val isReadOnly = intent.getBooleanExtra(BLUETOOTH_READ_STATE, false)
                    add(QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.ic_bluetooth))
                }
                if(intent.getBooleanExtra(FLASHLIGHT, false)) {
                    val isReadOnly = intent.getBooleanExtra(FLASHLIGHT_READ_STATE, false)
                    add(QuickSettingModel.TilesModel(FLASHLIGHT, R.drawable.ic_flashlight))
                }
                if(intent.getBooleanExtra(GPS, false)) {
                    val isReadOnly = intent.getBooleanExtra(GPS_READ_STATE, false)
                    add(QuickSettingModel.TilesModel(GPS, R.drawable.ic_gps_off))
                }
                if(intent.getBooleanExtra(MOBILEDATA, false)) {
                    val isReadOnly = intent.getBooleanExtra(MOBILEDATA_READ_STATE, false)
                    add(QuickSettingModel.TilesModel(MOBILEDATA, R.drawable.ic_cell_wifi))
                }
                if(intent.getBooleanExtra(NFC, false)) {
                    val isReadOnly = intent.getBooleanExtra(NFC_READ_STATE, false)
                    add(QuickSettingModel.TilesModel(NFC, R.drawable.ic_nfc))
                }
            }
            RxBus.publish(ConfigSetting(list))

            saveIntoSharedPreference(sharedpreferences, list)
        }
    }

    private fun saveIntoSharedPreference(sharedpreferences: SharedPreferences?, list: List<QuickSettingModel.TilesModel>) {
        val json: String = Gson().toJson(list)
        sharedpreferences?.edit()?.apply {
            putString(QSETTING_CONFIG, json)
        }?.commit()
    }
}


