package com.lpirro.tiledemo.sharing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lpirro.tiledemo.CloseQuickSetting
import com.lpirro.tiledemo.RxBus

class ExitQSettingReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        RxBus.publish(CloseQuickSetting)
    }
}