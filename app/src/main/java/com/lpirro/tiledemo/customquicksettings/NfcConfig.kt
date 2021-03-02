package com.lpirro.tiledemo.customquicksettings

import android.content.Context
import android.nfc.NfcManager
import android.util.Log
import com.lpirro.tiledemo.Utils


class NfcConfig(val context: Context) {

    private val manager by lazy {  context.getSystemService(Context.NFC_SERVICE) as NfcManager }


    fun initConfig(nfcState: (Boolean) -> Unit) {
        nfcState(manager.defaultAdapter.isEnabled)
    }

    @Throws(java.lang.Exception::class)
    fun changeNfcEnabled(enableOrDisable: Boolean, statelistener: (Boolean) -> Unit) {
        Log.d("TileDemo", " new command")
        var command: String? = null
        command = if (enableOrDisable) {
            "svc nfc enable"
        } else {
            "svc nfc disable"
        }
        Utils.executeCommandViaSu("-c", command, statelistener, enableOrDisable)
    }

}