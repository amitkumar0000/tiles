package com.lpirro.tiledemo

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.lpirro.tiledemo.databinding.ActivityScreenBinding
import java.lang.reflect.Method


class ScreenActivity : AppCompatActivity() {
    val mParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            200,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT)

    val binding by lazy { ActivityScreenBinding.inflate(LayoutInflater.from(this))}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_screen)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
//                startActivityForResult(intent, 12345)
//            }
//        }
//
//        mParams.gravity = Gravity.TOP
//
//        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        wm.addView(binding.root, mParams)



        val decorView = getWindow().getDecorView() as View
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.systemUiVisibility = uiOptions

        getWindow().getDecorView().setSystemUiVisibility(View.GONE);

        val mDPM: DevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val  mAdminName: ComponentName = ComponentName(this, DeviceAdminDemo::class.java)

        try {
            if (!mDPM.isAdminActive(mAdminName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                setKioskPolicies(mDPM, mAdminName)
            }
        }catch (e: Exception){
            e.printStackTrace();
        }

        if (mDPM.isDeviceOwnerApp(packageName)) {
            // You are the owner!
            setKioskPolicies(mDPM, mAdminName)
        } else {
            // Please contact your system administrator
        }

        binding.topScren.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Log.d("TAG", "Status bar touched")
                return true
            }

        })

        binding.topScren.setOnDragListener(object : View.OnDragListener {
            override fun onDrag(v: View?, event: DragEvent?): Boolean {
                Log.d("TAG", "Drag bar touched")
                return true
            }

        })

//        startLockTask()

    }

    private fun setKioskPolicies(mDevicePolicyManager: DevicePolicyManager, mAdminComponentName: ComponentName) {
//        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            Thread {
                while (!hasWindowFocus()) {
                    // Close every kind of system dialog
                    val closeDialog = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                    println("Thread Running: 'Kiosk Close system dialog'")
                    sendBroadcast(closeDialog)
                    try {
                        Thread.sleep(5)
                    } catch (e: InterruptedException) {
                        println("Thread interrupted: 'Kiosk Close system dialog'")
                    }
                }
            }.start()
        }
    }


}