package com.lpirro.tiledemo.sharing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lpirro.tiledemo.Utils
import com.lpirro.tiledemo.customquicksettings.QuickSettingModel

class ShareConfigReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedpreferences by lazy {  context?.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE) }

        val type = object : TypeToken<List<QuickSettingModel.TilesModel?>?>() {}.type
        val json: String? = sharedpreferences?.getString(Utils.QSETTING_CONFIG, "")

        val tilesList: List<QuickSettingModel.TilesModel> = Gson().fromJson(json, type)

        val listOfConfig: ArrayList<String> = if(tilesList != null && tilesList.isNotEmpty()) {
            val list = arrayListOf<String>()
            tilesList.forEach {
                list.add(it.name)
            }
            list
        }else {
            arrayListOf(WIFI, BLUETOOTH, GPS, FLASHLIGHT, NFC, MOBILEDATA)
        }

        context?.sendBroadcast(Intent().apply {
            action = "android.intent.action.qsetting.config_response"
            putStringArrayListExtra(Utils.QSETTING_CONFIG, listOfConfig)
        })
    }
}