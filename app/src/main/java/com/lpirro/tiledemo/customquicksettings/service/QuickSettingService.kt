package com.lpirro.tiledemo.customquicksettings.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.RxBus
import com.lpirro.tiledemo.customquicksettings.*
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding


class QuickSettingService : Service() {

    private var binding: ActivityCustomQuikSettingBinding? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    private val tilesAdapter by lazy { TilesAdapater() }

    init {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        init()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            attachForegroundNotification()
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun attachForegroundNotification() {
        val NOTIFICATION_CHANNEL_ID = "com.lpirro.tiledemo"
        val channelName = "My Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan);


        val notificationIntent = Intent(this, QuickSettingService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
// 1
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("QuickSetting")
                .setContentText("QuickSetting is running")
                .setSmallIcon(R.drawable.ic_tile_calendar)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(123, notification)
    }


    private fun init() {
        if(binding == null) {
            binding = ActivityCustomQuikSettingBinding.inflate(LayoutInflater.from(this))

            initQuickSettingTiles()
            showQuickSettingMenu()
            observeNotification()
        }
    }

    private fun observeNotification() {
        RxBus.listen().subscribe({
            tilesAdapter.setData(listOf(it))
        }, {

        })
    }

    private fun initQuickSettingTiles() {
        binding?.customQuickSetting?.adapter = tilesAdapter

        val layoutManager = GridLayoutManager(this, 4)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(tilesAdapter.getViewType(position)) {
                    TILES -> {
                        1
                    }
                    NOTIFICATIOn -> {
                        4
                    }
                    else -> {
                        throw IllegalArgumentException("View holder of this position not supported")
                    }
                }
            }
        }

        binding?.customQuickSetting?.layoutManager = layoutManager
//        binding?.customQuickSetting?.suppressLayout(true)

        tilesAdapter.setData(
                listOf(
                        QuickSettingModel.TilesModel(WIFI, R.drawable.wifi_off),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.bluetooth),
                        QuickSettingModel.TilesModel(AIRPLANE, R.drawable.airplanemode_inactive),
                        QuickSettingModel.TilesModel(WIFI, R.drawable.wifi_off),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.bluetooth),
                        QuickSettingModel.TilesModel(AIRPLANE, R.drawable.airplanemode_inactive),
                        QuickSettingModel.TilesModel(WIFI, R.drawable.wifi_off),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.bluetooth),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.bluetooth),
                        QuickSettingModel.NotificationModel("Bluetooth", "Switch on Bluetooth", R.drawable.bluetooth)
                )
        )

        binding?.customQuickSetting?.isVerticalScrollBarEnabled = false

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapater = tilesAdapter))
        itemTouchHelper.attachToRecyclerView(binding?.customQuickSetting)
    }

    private fun showQuickSettingMenu() {

        if(binding!!.root.isShown)
            return

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        localLayoutParams.gravity = Gravity.TOP
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or  // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or  // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        localLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(binding!!.root, localLayoutParams)

    }
}