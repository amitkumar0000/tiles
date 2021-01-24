package com.lpirro.tiledemo.customquicksettings

import android.app.ActivityManager
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.androidbolts.topsheet.TopSheetBehavior
import com.lpirro.tiledemo.CustomViewGroup
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding


class CustomQuikSettingActivity : AppCompatActivity() {

    private var binding: ActivityCustomQuikSettingBinding? = null

    private val adapater by lazy { TilesAdapater() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomQuikSettingBinding.inflate(LayoutInflater.from(this))
//        setContentView(binding!!.root)

        binding?.customQuickSetting?.adapter = adapater

        val layoutManager = GridLayoutManager(this, 3)
        binding?.customQuickSetting?.layoutManager = layoutManager
        binding?.customQuickSetting?.suppressLayout(true)

        adapater.setData(listOf(R.drawable.wifi_off,
                R.drawable.wifi_on,
                R.drawable.airplanemode_active,
                R.drawable.wifi_off,
                R.drawable.wifi_on,
                R.drawable.airplanemode_active)
        )

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            100 -> {
                blockStat()
            }
        }
    }

    fun addSystemOverlay() {
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.RIGHT or Gravity.TOP
        params.title = "Load Average"
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        TopSheetBehavior.from(binding!!.topSheet).setState(TopSheetBehavior.STATE_EXPANDED);

        wm.addView(binding!!.topSheet, params)
    }

    private fun blockStat() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager

        val statusBarOverlay = CustomViewGroup(this)

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        localLayoutParams.gravity = Gravity.TOP
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or  // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or  // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        localLayoutParams.height = 300
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(binding!!.root, localLayoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}