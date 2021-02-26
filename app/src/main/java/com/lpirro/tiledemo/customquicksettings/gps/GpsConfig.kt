package com.lpirro.tiledemo.customquicksettings.gps

import android.R
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.PixelFormat
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.lpirro.tiledemo.customquicksettings.service.QuickSettingService
import com.lpirro.tiledemo.databinding.TextInpuPasswordBinding
import java.io.DataOutputStream


class GpsConfig(val context: Context, val windowManager: WindowManager) {
    private val alertbinding by lazy {  TextInpuPasswordBinding.inflate(LayoutInflater.from(context)) }
    private var googleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 199

    fun changeGpsEnabled(enableOrDisable: Boolean, statelistener: (Boolean) -> Unit) {
        val p = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(p.outputStream)
        if(enableOrDisable) {
            os.writeBytes("settings put secure location_providers_allowed +gps" + "\n")
            statelistener(true)
        }else {
            os.writeBytes("settings put secure location_providers_allowed -gps" + "\n")
            statelistener(false)
        }
        os.writeBytes("exit\n")
        os.flush()
    }

    fun addNotification() {
        val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        val channelName = "My Background Service test"
        val chan = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan);


        val notificationIntent = Intent(context, QuickSettingService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, notificationIntent, 0)
// 1
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Hello App")
                .setContentText("My QuickSetting is running")
                .setSmallIcon(com.lpirro.tiledemo.R.drawable.ic_wifi)
                .setContentIntent(pendingIntent)
                .build()

        manager.notify(0, notification)
    }

    fun configGpsConfig(enableOrDisable: Boolean, statelistener: (Boolean) -> Unit) {
        enableLocationSettings()
    }

    fun statusCheck() {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            addAlertDialog()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    protected fun enableLocationSettings() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val isGpsProviderEnabled: Boolean
        val isNetworkProviderEnabled: Boolean
        isGpsProviderEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkProviderEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Location Permission")
            builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.")
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            builder.setNegativeButton(R.string.no, null)
            builder.show()
        }
    }

    fun  addAlertDialog() {

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        localLayoutParams.gravity = Gravity.CENTER
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL  // this is to enable the notification to receive touch events


        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        localLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(alertbinding!!.root, localLayoutParams)

        alertbinding.btnOk.setOnClickListener {
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            })
        }

        alertbinding.btnCancel.setOnClickListener {
            windowManager.removeView(alertbinding!!.root)
        }

        alertbinding.parentDialogLayout.setOnClickListener {
            windowManager.removeView(alertbinding!!.root)
        }
    }
}