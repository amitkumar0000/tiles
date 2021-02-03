package com.lpirro.tiledemo.customquicksettings.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BluetoothReceiver(): BroadcastReceiver() {
    private var bluetoothState: ((Boolean)-> Unit) ?= null

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action

        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent!!.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    bluetoothState?.let {
                        it(false)
                    }
                }
                BluetoothAdapter.STATE_TURNING_OFF -> {
                }
                BluetoothAdapter.STATE_ON -> {
                    bluetoothState?.let {
                        it(true)
                    }
                }
                BluetoothAdapter.STATE_TURNING_ON -> {
                }
            }
        }
    }

    fun setListener(bluetoothState: (Boolean) -> Unit) {
        this.bluetoothState = bluetoothState
    }
}