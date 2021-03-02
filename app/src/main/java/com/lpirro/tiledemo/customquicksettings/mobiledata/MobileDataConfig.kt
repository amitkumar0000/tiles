package com.lpirro.tiledemo.customquicksettings.mobiledata

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.lpirro.tiledemo.Utils
import java.io.IOException
import java.util.*


class MobileDataConfig(val context: Context) {
    private val telephonyService by lazy {  context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }

    fun setMobileDataState__(enable: Boolean, statelistener: (Boolean) -> Unit) {
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                telephonyService?.isDataEnabled = enable
                statelistener(true)
            }
        } catch (ex: Exception) {
            Log.e("MainActivity", "Error setting mobile data state", ex)
            statelistener(false)
        }
    }

    fun initConfig(mobileDataState: (Boolean) -> Unit) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            mobileDataState(telephonyService.isDataEnabled)
//        }
    }

    @Throws(java.lang.Exception::class)
    fun setMobileDataState(enableOrDisable: Boolean, statelistener: (Boolean) -> Unit) {
        Log.d("TileDemo", " new command")
        var command: String? = null
        command = if (enableOrDisable) {
            "svc data enable"
        } else {
            "svc data disable"
        }
        Utils.executeCommandViaSu("-c", command, statelistener, enableOrDisable)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(activity: Activity) {
        if(context.checkSelfPermission(Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
            )
        }
    }

}