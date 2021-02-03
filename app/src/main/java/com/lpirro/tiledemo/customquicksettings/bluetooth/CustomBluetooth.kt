package com.lpirro.tiledemo.customquicksettings.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentFilter

class CustomBluetooth(val context: Context) {

    private val bluetoothReceiver by lazy {  BluetoothReceiver() }

    init {
        context.registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun configure( state: Boolean, bluetoothState: (Boolean) -> Unit) {
        setBluetooth(state, bluetoothState)
        bluetoothReceiver.setListener(bluetoothState)
    }

    fun unregister() {
        context.unregisterReceiver(bluetoothReceiver)
    }

    private fun setBluetooth(enable: Boolean, bluetoothState: (Boolean) -> Unit): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val isEnabled = bluetoothAdapter.isEnabled
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable()
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable()
        }
        // No need to change bluetooth state
        bluetoothState(true)
        return true
    }
}
