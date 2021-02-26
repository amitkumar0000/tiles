package com.lpirro.tiledemo

import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE
import android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.lpirro.tiledemo.customquicksettings.service.QuickSettingService
import com.lpirro.tiledemo.databinding.ActivityDeviceAdminBinding
import io.reactivex.Observable
import java.io.DataOutputStream
import java.util.concurrent.TimeUnit


class DeviceAdminActivity : AppCompatActivity() {
    val mDevicePolicyManager: DevicePolicyManager by lazy {  getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val  mComponentName: ComponentName by lazy { ComponentName(this, DeviceAdminDemo::class.java) }
    val  binding by lazy { ActivityDeviceAdminBinding.inflate(LayoutInflater.from(this)) }

    var toggle = false
    val sharedpreferences by lazy {  getSharedPreferences("MyPREFERENCES", MODE_PRIVATE) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(!sharedpreferences.getBoolean("DISABLE_STATE", false)) {
            disableSystemUi()
        }
//        setupProfile()
//        setDeviceAdmin()

//        disableSystemUi()
        startService(Intent(this, QuickSettingService::class.java))
        finish()

//        binding.button.setOnClickListener {
//            if(!toggle) {
//                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
////                    mDevicePolicyManager.setStatusBarDisabled(mComponentName, true)
//
//
//
//                    createNotificationPermission()
//                }
//            } else {
////                mDevicePolicyManager.setStatusBarDisabled(mComponentName, false)
//            }
//        }
    }


    private fun createNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {        //ask for permission
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }
        }
    }

    private fun disableSystemUi() {
        Runtime.getRuntime().exec("su")

        sharedpreferences.edit().apply {
            putBoolean("DISABLE_STATE", true)
        }.commit()

        Observable.timer(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe({
                    Log.d("Amit", " Disabling system ui")
                    val p = Runtime.getRuntime().exec("su")
                    val os = DataOutputStream(p.outputStream)
                    os.writeBytes("pm disable com.android.systemui" + "\n")
                    os.writeBytes("reboot" + "\n")
                    os.writeBytes("exit\n")
                    os.flush()

                    finish()
                }, {})

    }


    private fun setupProfile() {
        val intent =  Intent(ACTION_PROVISION_MANAGED_PROFILE)
        intent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                applicationContext.packageName);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 200);
        } else {
            Toast.makeText(this, "Stopping.", Toast.LENGTH_SHORT).show();
        }
    }

    private fun setDeviceAdmin() {
        try {
            if (!mDevicePolicyManager.isAdminActive(mComponentName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
//                mDevicePolicyManager.setStatusBarDisabled(mComponentName, true)
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
//                mDevicePolicyManager.setStatusBarDisabled(mComponentName, true)
            } else {
                // cancle it.
            }
        } else if (REQUEST_CODE == 200) {
//            setDeviceAdmin()
        }
    }
}