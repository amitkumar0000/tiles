package com.lpirro.tiledemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lpirro.tiledemo.customquicksettings.service.QuickSettingService

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("STATUS"," Boot complete received")
        context?.startService(Intent(context, QuickSettingService::class.java))
    }
}