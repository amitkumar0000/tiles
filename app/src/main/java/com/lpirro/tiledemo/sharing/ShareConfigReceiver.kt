package com.lpirro.tiledemo.sharing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lpirro.tiledemo.Utils
import com.lpirro.tiledemo.customquicksettings.QuickSettingModel
import kotlinx.android.parcel.Parcelize

class ShareConfigReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null)
            return
        val sharedpreferences = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE)
        context.sendBroadcast(Intent().apply {
            action = "android.intent.action.qsetting.config_response"
            putExtra("config_bundle", Bundle().apply {
                putParcelable("cofig", Config(shownElements =  getShownElements(sharedpreferences), shownNotifications = getNotification(sharedpreferences)))
            })
        })
    }
}

private fun getShownElements(sharedPreferences: SharedPreferences): ShownElements {
    val type = object : TypeToken<List<QuickSettingModel.TilesModel?>?>() {}.type
    val json: String? = sharedPreferences?.getString(Utils.QSETTING_CONFIG, "")
    val tilesList: List<QuickSettingModel.TilesModel> = Gson().fromJson(json, type)
    val shownElements = ShownElements(arrayListOf())

    if (!json.isNullOrEmpty()) {
        tilesList.forEach {
            val readState = if (it.isReadOnly)
                "readOnly"
            else
                "readWrite"
            shownElements.elements.add(Elements(elementName = it.name, accessLevel = readState))
        }
        return shownElements
    }
    return getDefaultShownElements()
}

private fun getDefaultShownElements():ShownElements {
    var elements = ArrayList<Elements>()
    elements.add(Elements(WLAN, "readWrite"))
    elements.add(Elements(Bluetooth, "readWrite"))
    elements.add(Elements(Flashlight, "readWrite"))
    elements.add(Elements(GPS, "readWrite"))
    elements.add(Elements(MOBILEDATA, "readWrite"))
    elements.add(Elements(NFC, "readWrite"))

    return ShownElements(elements)
}

private fun getNotification(sharedPreferences: SharedPreferences): ShownNotifications {
    val type = object : TypeToken<List<Notification?>?>() {}.type
    val json: String? = sharedPreferences?.getString(Utils.NOTIFICATION_PACKAGE_LIST, "")
    if (json != null) {
        val notificationList: List<Notification> = Gson().fromJson(json, type)
        return ShownNotifications(notification = notificationList)
    } else {
        return ShownNotifications(notification = listOf())
    }
}

