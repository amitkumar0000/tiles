package com.lpirro.tiledemo.sharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lpirro.tiledemo.CloseQuickSetting
import com.lpirro.tiledemo.RxBus

class ExitQSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RxBus.publish(CloseQuickSetting)
        finish()

    }
}