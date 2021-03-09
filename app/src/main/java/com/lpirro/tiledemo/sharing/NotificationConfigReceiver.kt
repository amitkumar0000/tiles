package com.lpirro.tiledemo.sharing

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lpirro.tiledemo.Utils
import com.lpirro.tiledemo.customquicksettings.QuickSettingModel

class NotificationConfigReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedpreferences by lazy {  context?.getSharedPreferences("MyPREFERENCES", Service.MODE_PRIVATE) }

        intent?.getStringArrayListExtra(Utils.NOTIFICATION_PACKAGE_LIST)?.let {
           Utils.listOfallowedNotificationPackage.addAll(it)

           saveToSharedPreference(sharedpreferences, it)
       }
    }

    private fun saveToSharedPreference(sharedpreferences: SharedPreferences?, list: ArrayList<String>) {
        val json: String = Gson().toJson(list)
        sharedpreferences?.edit()?.apply {
            putString(Utils.NOTIFICATION_PACKAGE_LIST, json)
        }?.commit()
    }
}