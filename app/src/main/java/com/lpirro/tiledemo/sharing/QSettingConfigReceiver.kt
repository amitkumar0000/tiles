package com.lpirro.tiledemo.sharing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.lpirro.tiledemo.ConfigSetting
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.RxBus
import com.lpirro.tiledemo.Utils
import com.lpirro.tiledemo.Utils.QSETTING_CONFIG
import com.lpirro.tiledemo.customquicksettings.QuickSettingModel


const val WLAN = "WLAN"
const val Bluetooth = "Bluetooth"
const val Flashlight = "Flashlight"
const val GPS = "GPS"
const val MOBILEDATA = "MOBILE DATA"
const val NFC = "NFC"

class QSettingConfigReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            intent?.let {
                val configBundle: Bundle? = it.getBundleExtra("config_bundle")
                configBundle?.let {
                    val config = it.getParcelable("config") as? Config
                    config?.let { saveConfig(context!!, config) }
                }
            }
        }catch (e: Exception) {
            Toast.makeText(context!!," check param is passed in bundle with key config_bundle and the Parcalable object with key config", Toast.LENGTH_LONG ).show()
        }
    }

    private fun saveConfig(context: Context, config: Config) {
        val sharedpreferences by lazy {  context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE) }

        val list = ArrayList<QuickSettingModel.TilesModel>().apply {

            val shownElements = config.shownElements
            val shownNotifications = config.shownNotifications

            shownElements.elements.forEach {
                when(it.elementName) {
                    WLAN -> {
                        add(QuickSettingModel.TilesModel(WLAN, R.drawable.ic_wifi, isReadOnly = it.accessLevel == "readonly"))
                    }
                    Bluetooth -> {
                        add(QuickSettingModel.TilesModel(Bluetooth, R.drawable.ic_bluetooth, isReadOnly = it.accessLevel == "readonly"))
                    }
                    Flashlight -> {
                        add(QuickSettingModel.TilesModel(Flashlight, R.drawable.ic_flashlight, isReadOnly = it.accessLevel == "readonly"))
                    }
                    GPS -> {
                        add(QuickSettingModel.TilesModel(GPS, R.drawable.ic_gps_off, isReadOnly = it.accessLevel == "readonly"))
                    }
                    MOBILEDATA -> {
                        add(QuickSettingModel.TilesModel(MOBILEDATA, R.drawable.ic_cell_wifi, isReadOnly = it.accessLevel == "readonly"))
                    }
                    NFC -> {
                        add(QuickSettingModel.TilesModel(NFC, R.drawable.ic_nfc, isReadOnly = it.accessLevel == "readonly"))
                    }
                }
            }

            val listOfNotifications = arrayListOf<Notification>()
            shownNotifications.notification.forEach {
                listOfNotifications.add(it)
            }

            Utils.listOfallowedNotificationPackage.addAll(listOfNotifications.map { it.packageName })
            saveToSharedPreference(sharedpreferences, listOfNotifications)
        }
        RxBus.publish(ConfigSetting(list))
        saveIntoSharedPreference(sharedpreferences, list)
    }

    private fun saveToSharedPreference(sharedpreferences: SharedPreferences?, list: ArrayList<Notification>) {
        val json: String = Gson().toJson(list)
        sharedpreferences?.edit()?.apply {
            putString(Utils.NOTIFICATION_PACKAGE_LIST, json)
        }?.commit()
    }

    private fun saveIntoSharedPreference(sharedpreferences: SharedPreferences?, list: List<QuickSettingModel.TilesModel>) {
        val json: String = Gson().toJson(list)
        sharedpreferences?.edit()?.apply {
            putString(QSETTING_CONFIG, json)
        }?.commit()
    }


}



