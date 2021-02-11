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
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.getSystemService
import java.lang.reflect.Method
import java.security.Permission
import java.util.*


class MobileDataConfig(val context: Context) {
    fun setMobileDataState(enable: Boolean, statelistener: (Boolean) -> Unit) {
        try {
            val telephonyService = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                telephonyService?.isDataEnabled = enable
                statelistener(true)
            }
        } catch (ex: Exception) {
            Log.e("MainActivity", "Error setting mobile data state", ex)
            statelistener(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(activity: Activity) {
        if(context.checkSelfPermission(Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
            )
        }
    }

}