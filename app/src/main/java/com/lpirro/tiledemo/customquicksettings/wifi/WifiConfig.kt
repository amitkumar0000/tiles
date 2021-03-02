package com.lpirro.tiledemo.customquicksettings.wifi

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager

class WifiConfig(val context: Context) {
    private val wifiManager by lazy {  context.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    fun initConfig(wifiState: (Boolean) -> Unit) {
        wifiState(wifiManager.isWifiEnabled)
    }

    fun configWifi(state: Boolean) {
        wifiManager.isWifiEnabled = state
    }
}