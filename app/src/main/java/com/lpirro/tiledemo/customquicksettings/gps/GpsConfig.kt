package com.lpirro.tiledemo.customquicksettings.gps

import android.content.Context
import android.location.LocationManager
import android.view.WindowManager
import java.io.DataOutputStream


class GpsConfig(val context: Context, val windowManager: WindowManager) {
    private var lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun initConfig(gpsState: (Boolean) -> Unit) {
        gpsState(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    fun changeGpsEnabled(enableOrDisable: Boolean, statelistener: (Boolean) -> Unit) {
        val p = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(p.outputStream)
        if(enableOrDisable) {
            os.writeBytes("settings put secure location_providers_allowed +gps" + "\n")
            statelistener(true)
        }else {
            os.writeBytes("settings put secure location_providers_allowed -gps" + "\n")
            statelistener(false)
        }
        os.writeBytes("exit\n")
        os.flush()
    }

}