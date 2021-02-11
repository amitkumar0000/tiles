package com.lpirro.tiledemo.customquicksettings.service

import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.lpirro.tiledemo.DeviceAdminDemo
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.customquicksettings.*
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding
import com.lpirro.tiledemo.databinding.TextInpuPasswordBinding


class QuickSettingService : Service() {

    private var binding: ActivityCustomQuikSettingBinding? = null
    private val windowManager by lazy {  getSystemService(WINDOW_SERVICE) as WindowManager }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    val mDevicePolicyManager: DevicePolicyManager by lazy {  getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val  mComponentName: ComponentName by lazy { ComponentName(this, DeviceAdminDemo::class.java) }

    private val tilesAdapter by lazy { TilesAdapter(this) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            attachForegroundNotification()
            init()
            Log.d("STATUS", " attachForegroundNotification complete received")

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
        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0)
// 1
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("QuickSetting")
                .setContentText("QuickSetting is running")
                .setSmallIcon(R.drawable.ic_wifi)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(123, notification)
    }


    private fun init() {
        if(binding == null) {
            binding = ActivityCustomQuikSettingBinding.inflate(LayoutInflater.from(this))

            initQuickSettingTiles()
            showQuickSettingMenu()

            stopQuickSetting()
            observeNotification()
        }
    }

    private fun stopQuickSetting() {
//        binding!!.menuOption.setOnClickListener {
//
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                mDevicePolicyManager.setStatusBarDisabled(mComponentName, false)
//            }
//            stopForeground(true)
//            stopSelf()
//
//        }
    }

    private fun observeNotification() {
//        RxBus.listen().subscribe({
//            tilesAdapter.setData(listOf(it))
//        }, {
//
//        })
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
                    BRIGHTNESS -> {
                        4
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
                        QuickSettingModel.TilesModel(WIFI, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.ic_bluetooth),
                        QuickSettingModel.TilesModel(FLASHLIGHT, R.drawable.ic_flashlight),

                        QuickSettingModel.TilesModel(GPS, R.drawable.ic_gps_off),
                        QuickSettingModel.TilesModel(MOBILEDATA, R.drawable.ic_cell_wifi),
                        QuickSettingModel.TilesModel(NFC, R.drawable.ic_nfc),
//                        QuickSettingModel.BrightnessModel,
//                        QuickSettingModel.NotificationModel("Bluetooth", "Switch on Bluetooth", R.drawable.wifi_on_state)
                )
        )

        binding?.customQuickSetting?.isVerticalScrollBarEnabled = false

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter = tilesAdapter))
        itemTouchHelper.attachToRecyclerView(binding?.customQuickSetting)
    }

    private fun showQuickSettingMenu() {

        if(binding!!.root.isShown)
            return

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

        binding?.quickSettingStatus?.exitImageView?.setOnClickListener {
            addAlertDialog()
        }
    }

    fun  addAlertDialog() {
        val binding = TextInpuPasswordBinding.inflate(LayoutInflater.from(this))

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        localLayoutParams.gravity = Gravity.CENTER
        localLayoutParams.flags = FLAG_NOT_TOUCH_MODAL  // this is to enable the notification to receive touch events


        localLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        localLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(binding!!.root, localLayoutParams)

        binding.btnOk.setOnClickListener {
            val text = binding!!.editTextTextPassword.text.toString()
            if(text == "ais1997") {
                windowManager.removeView(binding!!.root)
                closeQuickSettingMenu()
            }
        }

        binding.btnCancel.setOnClickListener {
            windowManager.removeView(binding!!.root)
        }

    }


    fun closeQuickSettingMenu() {
        windowManager.removeView(binding!!.root)
        stopForeground(true)
        mDevicePolicyManager.setStatusBarDisabled(mComponentName, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        tilesAdapter.onCleared()
    }
}