package com.lpirro.tiledemo.customquicksettings.flashlight

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.lpirro.tiledemo.DeviceAdminDemo


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class FlashLightConfig(val context: Context) {
    private val mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val mCameraId = mCameraManager.getCameraIdList()[0]

    fun initConfig(listener: (Boolean) -> Unit) {
        try {
            
        }catch (exception: Exception) {
            listener(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun configFlashLight(state: Boolean, listener: (Boolean) -> Unit) {
        try {
            mCameraManager.setTorchMode(mCameraId, state)
            listener(state)
        }catch (exception: Exception) {
            listener(false)
        }
    }
}