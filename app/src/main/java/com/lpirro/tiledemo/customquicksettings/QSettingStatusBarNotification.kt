package com.lpirro.tiledemo.customquicksettings

import android.app.Notification
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.lpirro.tiledemo.ClearAllNotification
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.RxBus
import com.lpirro.tiledemo.Utils


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class QSettingStatusBarNotification: NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        registerListener()
    }

    private fun registerListener() {
        RxBus.listen().subscribe {
            when(it) {
                is ClearAllNotification -> {
                    cancelAllNotifications()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d("STATUS", "${sbn?.packageName}  ${sbn?.notification.toString()}")


        sbn?.notification?.let {

            val extra = it.extras
            val title = extra.get("android.title") as String?
            val text = extra.get("android.text") as String?

            Log.d("STATUS title", ":- $title ")
            Log.d("STATUS text", ":-  $text")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Log.d("STATUS small icon", ":-  ${it.smallIcon}i")
                Log.d("STATUS large icon", ":-  ${it.getLargeIcon()}")
            }

            if(!Utils.listOfallowedNotificationPackage.contains(sbn.packageName)) {
                Log.d("STATUS", "notification is blocked for packageName:- ${sbn.packageName}")
                return
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                RxBus.publishNotification(NotificationModel(icon = it.smallIcon, title = title, content = text, pendingIntent = it.contentIntent))
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }


}