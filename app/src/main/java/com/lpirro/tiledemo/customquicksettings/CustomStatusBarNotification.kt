package com.lpirro.tiledemo.customquicksettings

import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.lpirro.tiledemo.RxBus


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class CustomStatusBarNotification: NotificationListenerService() {

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
            } else {
                TODO("VERSION.SDK_INT < M")
            }

            if (sbn?.packageName.equals("com.example.testsample")) {
                Log.d("STATUS"," Notiification blocked ${sbn?.packageName}")
                return
            }


//            RxBus.publish(QuickSettingModel.NotificationModel(title ?: "", text ?: "", 0, it.smallIcon))

            cancelAllNotifications()
        }

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }


}