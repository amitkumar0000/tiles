package com.lpirro.tiledemo.customquicksettings

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class CustomQuikSettingActivity : AppCompatActivity() {

    private var binding: ActivityCustomQuikSettingBinding? = null

    private val tilesAdapter by lazy { TilesAdapater() }
    private val notificationAdapter by lazy { NotificationAdapter() }

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
    }

    private fun initQuickSettingTiles() {
        binding?.customQuickSetting?.adapter = tilesAdapter

        val layoutManager = GridLayoutManager(this, 3)
        binding?.customQuickSetting?.layoutManager = layoutManager
        binding?.customQuickSetting?.suppressLayout(true)

        tilesAdapter.setData(listOf(R.drawable.wifi_off,
                R.drawable.wifi_on,
                R.drawable.airplanemode_active,
                R.drawable.wifi_off,
                R.drawable.wifi_on,
                R.drawable.airplanemode_active)
        )

        binding?.customQuickSetting?.isVerticalScrollBarEnabled = false
    }

    private fun initNotification() {
        binding?.notificationList?.adapter = notificationAdapter

        binding?.notificationList?.layoutManager = LinearLayoutManager(this)

        notificationAdapter.setData(listOf(
                NotificationModel(R.drawable.bluetooth, "Bluetooth", "Switch on Bluetooth"),
                NotificationModel(R.drawable.bluetooth, "Bluetooth", "Switch on Bluetooth"),
                NotificationModel(R.drawable.bluetooth, "Bluetooth", "Switch on Bluetooth")
        ))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            100 -> {
                blockStat()
            }
        }
    }

    private fun blockStat() {
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

    private fun slideWithTopSheet() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binding?.topSheet?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}