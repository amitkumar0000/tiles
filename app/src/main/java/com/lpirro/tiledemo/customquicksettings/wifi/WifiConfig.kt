package com.lpirro.tiledemo.customquicksettings.wifi

import android.content.Context
import android.net.wifi.WifiManager

class WifiConfig(val context: Context) {

    fun configWifi(state: Boolean) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = state
    }
}