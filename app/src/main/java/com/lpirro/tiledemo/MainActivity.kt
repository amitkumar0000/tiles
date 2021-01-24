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
val NOTIFICATION_REQUEST_CODE = 1;

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        block()
    }

    fun block() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
                startActivityForResult(intent, NOTIFICATION_REQUEST_CODE);
            } else {
                blockStat();
            }
        }
        else {
            blockStat()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            100 -> blockStat()
            NOTIFICATION_REQUEST_CODE -> {

            }
        }
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
