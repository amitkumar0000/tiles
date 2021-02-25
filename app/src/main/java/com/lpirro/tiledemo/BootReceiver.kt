package com.lpirro.tiledemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lpirro.tiledemo.customquicksettings.service.QuickSettingService
import io.reactivex.Observable
import java.io.DataOutputStream
import java.util.concurrent.TimeUnit

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("STATUS"," Boot complete received")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context?.startForegroundService(Intent(context, QuickSettingService::class.java))
        } else {
            context?.startService(Intent(context, QuickSettingService::class.java))

        }
    }
}