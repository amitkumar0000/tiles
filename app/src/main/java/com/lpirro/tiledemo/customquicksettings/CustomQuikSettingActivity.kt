package com.lpirro.tiledemo.customquicksettings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidbolts.topsheet.TopSheetBehavior
import com.androidbolts.topsheet.TopSheetDialog
import com.lpirro.tiledemo.R
import com.lpirro.tiledemo.databinding.ActivityCustomQuikSettingBinding

class CustomQuikSettingActivity : AppCompatActivity() {

    private var binding: ActivityCustomQuikSettingBinding? = null

    private val adapater by lazy { TilesAdapater() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomQuikSettingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        binding?.customQuickSetting?.adapter = adapater

        val layoutManager = GridLayoutManager(this, 3)
        binding?.customQuickSetting?.layoutManager = layoutManager

        adapater.setData(listOf(R.drawable.wifi_off,
                R.drawable.wifi_on,
                R.drawable.airplanemode_active,
                R.drawable.wifi_off,
                R.drawable.wifi_on,
                R.drawable.airplanemode_active)
        )

        TopSheetBehavior.from(binding!!.topSheet).setState(TopSheetBehavior.STATE_EXPANDED);

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}