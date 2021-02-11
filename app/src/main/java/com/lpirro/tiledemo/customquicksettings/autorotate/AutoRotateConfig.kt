package com.lpirro.tiledemo.customquicksettings.autorotate

import android.content.Context
import android.provider.Settings

class AutoRotateConfig(val context: Context) {
    fun config(enabled: Boolean) {
        Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, if (enabled) 1 else 0)
    }
}