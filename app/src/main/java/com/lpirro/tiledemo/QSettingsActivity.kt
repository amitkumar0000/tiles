package com.lpirro.tiledemo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lpirro.tiledemo.customquicksettings.service.QuickSettingService
import com.lpirro.tiledemo.databinding.ActivityDeviceAdminBinding
import io.reactivex.Observable
import java.io.DataOutputStream
import java.util.concurrent.TimeUnit

class QSettingsActivity : AppCompatActivity() {
    private val  binding by lazy { ActivityDeviceAdminBinding.inflate(LayoutInflater.from(this)) }
    private val sharedpreferences by lazy {  getSharedPreferences("MyPREFERENCES", MODE_PRIVATE) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(!sharedpreferences.getBoolean("DISABLE_STATE", false)) {
            disableSystemUi()
        }
        startService(Intent(this, QuickSettingService::class.java))
        finish()
    }

    private fun disableSystemUi() {
        Runtime.getRuntime().exec("su")

        sharedpreferences.edit().apply {
            putBoolean("DISABLE_STATE", true)
        }.commit()

        Observable.timer(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe({
                    val p = Runtime.getRuntime().exec("su")
                    val os = DataOutputStream(p.outputStream)
                    os.writeBytes("pm disable com.android.systemui" + "\n")
                    os.writeBytes("reboot" + "\n")
                    os.writeBytes("exit\n")
                    os.flush()

                    finish()
                }, {})
    }
}