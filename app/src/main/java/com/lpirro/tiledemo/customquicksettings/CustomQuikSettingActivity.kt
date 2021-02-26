package com.lpirro.tiledemo.customquicksettings

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.lpirro.tiledemo.DeviceAdminDemo
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.customquicksettings.service.QuickSettingService
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding


const val WIFI = "wifi"
const val BLUETOOTH = "bluetooth"
const val FLASHLIGHT = "flashlight"
const val GPS = "gps"
const val MOBILEDATA = "mobileData"
const val NFC = "nfc"
const val AIRPLANE = "airplane"
const val ROTATION = "ROTATION"

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class CustomQuikSettingActivity : AppCompatActivity() {

    private var binding: ActivityCustomQuikSettingBinding? = null

    private val tilesAdapter by lazy { TilesAdapter(this, windowManager) }
    private val notificationAdapter by lazy { NotificationAdapter(){} }

    val mDevicePolicyManager: DevicePolicyManager by lazy {  getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val  mComponentName: ComponentName by lazy { ComponentName(this, DeviceAdminDemo::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomQuikSettingBinding.inflate(LayoutInflater.from(this))
//        setContentView(binding!!.root)

        initQuickSettingTiles()
        initNotification()

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
                startActivityForResult(intent, 100);
            } else {
                blockStat()
            }
        }

        observeNotification()
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
                        QuickSettingModel.TilesModel(FLASHLIGHT, R.drawable.ic_flashlight),
                        QuickSettingModel.TilesModel(AIRPLANE, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(WIFI, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(AIRPLANE, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(WIFI, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.ic_wifi),
                        QuickSettingModel.TilesModel(BLUETOOTH, R.drawable.ic_wifi),
                        QuickSettingModel.BrightnessModel,
//                        QuickSettingModel.NotificationModel("Bluetooth", "Switch on Bluetooth", R.drawable.ic_wifi)
                )
        )

        binding?.customQuickSetting?.isVerticalScrollBarEnabled = false
    }

    private fun initNotification() {
//        binding?.notificationList?.adapter = notificationAdapter
//
//        binding?.notificationList?.layoutManager = LinearLayoutManager(this)
//
//        notificationAdapter.setData(listOf(
//                NotificationModel(R.drawable.bluetooth, "Bluetooth", "Switch on Bluetooth"),
//                NotificationModel(R.drawable.bluetooth, "Bluetooth", "Switch on Bluetooth"),
//                NotificationModel(R.drawable.bluetooth, "Bluetooth", "Switch on Bluetooth")
//        ))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            100 -> {
                blockStat()
            }
        }
    }

    private fun stopQuickSetting() {
//        binding!!.menuOption.setOnClickListener {
//
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                mDevicePolicyManager.setStatusBarDisabled(mComponentName, false)
//            }
//
//        }
    }

    private fun blockStat() {
        startService(Intent(this, QuickSettingService::class.java))
        finish()
    }

    private fun addQuickSettingMenu() {
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}