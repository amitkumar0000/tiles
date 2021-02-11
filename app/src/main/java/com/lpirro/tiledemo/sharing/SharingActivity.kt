package com.lpirro.tiledemo.sharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lpirro.tiledemo.CloseQuickSetting
import com.lpirro.tiledemo.RxBus

class SharingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rInent = intent

        val rPkgName = rInent.getStringExtra("Package_name")
        val exitQuickSetting = rInent.getBooleanExtra("CLOSE QUICKSETTING", false)
        rPkgName?.let {
            Log.d("STATUS"," Receiving package name: $it")
        }

        if(exitQuickSetting)
            RxBus.publish(CloseQuickSetting)

        val intent = Intent()

        intent.putExtra("Sender_pkg", "TileDemo")
        intent.putExtra("QUICK_SETTING_STATE", false)
        setResult(RESULT_OK, intent)

        finish()
    }
}