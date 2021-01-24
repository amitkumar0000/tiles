package com.lpirro.tiledemo

import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE
import android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.lpirro.tiledemo.customquicksettings.CustomQuikSettingActivity
import com.lpirro.tiledemo.databinding.ActivityDeviceAdminBinding


class DeviceAdminActivity : AppCompatActivity() {
    val mDevicePolicyManager: DevicePolicyManager by lazy {  getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val  mComponentName: ComponentName by lazy { ComponentName(this, DeviceAdminDemo::class.java) }
    val  binding by lazy { ActivityDeviceAdminBinding.inflate(LayoutInflater.from(this)) }

    var toggle = false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setupProfile()
        setDeviceAdmin()

        binding.button.setOnClickListener {
            if(!toggle) {
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mDevicePolicyManager.setStatusBarDisabled(mComponentName, true)

                    startActivity(Intent(this, CustomQuikSettingActivity::class.java))

                    createNotificationPermission()
                }
            } else {
                mDevicePolicyManager.setStatusBarDisabled(mComponentName, false)
            }
            toggle = !toggle
        }
    }

    private fun createNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {        //ask for permission
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }
        }
    }


    private fun setupProfile() {
        val intent =  Intent(ACTION_PROVISION_MANAGED_PROFILE)
        intent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                getApplicationContext().getPackageName());
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
                mDevicePolicyManager.setStatusBarDisabled(mComponentName, true)
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
                mDevicePolicyManager.setStatusBarDisabled(mComponentName, true)
            } else {
                // cancle it.
            }
        } else if (REQUEST_CODE == 200) {
            setDeviceAdmin()
        }
    }
}