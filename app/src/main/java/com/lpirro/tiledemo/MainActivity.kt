/*
 *
 *  * Copyright 2017 Leonardo Pirro
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.lpirro.tiledemo

import android.app.ActivityManager
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
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


val REQUEST_CODE = 0;

class MainActivity : AppCompatActivity() {
    val mDPM: DevicePolicyManager by lazy {  getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val  mAdminName: ComponentName by lazy { ComponentName(this, DeviceAdminDemo::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)



        try {
            if (!mDPM.isAdminActive(mAdminName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                mDPM.setStatusBarDisabled(mAdminName, false)

            }
        }catch (e: Exception){
            e.printStackTrace();
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE === requestCode) {
            if (requestCode == RESULT_OK) {
                // done with activate to Device Admin
                mDPM.setStatusBarDisabled(mAdminName, false)

                block()
            } else {
                // cancle it.
            }
        }
    }

    fun block() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
                startActivityForResult(intent, 100);
            } else {
                blockStat();
            }
        }
        else {
            blockStat()
        }
    }

    fun preventStatusBarExpansion() {
        val manager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val layoutParam = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        layoutParam.gravity = Gravity.TOP

//
//        val layoutParam = WindowManager.LayoutParams().also {
//            it.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
//            it.gravity = Gravity.TOP
//            it.flags = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
//        }
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var result = 0
        if (resId > 0) {
            result = resources.getDimensionPixelSize(resId)
        }
        layoutParam.height = result;

        layoutParam.format = PixelFormat.TRANSPARENT;


        val  view =  CustomViewGroup(this);
        manager.addView(view, layoutParam)
    }

    private fun blockStat() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager

        val statusBarOverlay = CustomViewGroup(this)

        val localLayoutParams = WindowManager.LayoutParams()

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        localLayoutParams.gravity = Gravity.TOP
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or  // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or  // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        localLayoutParams.height = (50 * resources.displayMetrics.scaledDensity).toInt()
        localLayoutParams.format = PixelFormat.TRANSPARENT

        windowManager.addView(statusBarOverlay, localLayoutParams)
    }
}
