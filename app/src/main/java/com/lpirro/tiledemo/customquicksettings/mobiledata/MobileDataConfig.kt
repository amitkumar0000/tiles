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
import java.io.IOException
import java.util.*


class MobileDataConfig(val context: Context) {
    fun setMobileDataState__(enable: Boolean, statelistener: (Boolean) -> Unit) {
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

    @Throws(java.lang.Exception::class)
    fun setMobileDataState(enableOrDisable: Boolean, statelistener: (Boolean) -> Unit) {
        Log.d("TileDemo", " new command")
        var command: String? = null
        command = if (enableOrDisable) {
            "svc data enable"
        } else {
            "svc data disable"
        }
        executeCommandViaSu(context, "-c", command, statelistener, enableOrDisable)
    }

    private fun executeCommandViaSu(context: Context, option: String, command: String, statelistener: (Boolean) -> Unit, enableOrDisable: Boolean) {
        var success = false
        var su = "su"
        for (i in 0..2) {
            // Default "su" command executed successfully, then quit.
            if (success) {
                break
            }
            // Else, execute other "su" commands.
            if (i == 1) {
                su = "/system/xbin/su"
            } else if (i == 2) {
                su = "/system/bin/su"
            }
            try {
                // Execute command as "su".
                Runtime.getRuntime().exec(arrayOf(su, option, command))
            } catch (e: IOException) {
                success = false
                // Oops! Cannot execute `su` for some reason.
                // Log error here.
                statelistener(false)
            } finally {
                success = true
                statelistener(enableOrDisable)
            }
        }
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