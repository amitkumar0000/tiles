package com.lpirro.tiledemo.customquicksettings

import android.content.Context
import android.nfc.NfcAdapter
import android.util.Log
import com.lpirro.tiledemo.Utils
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class NfcConfig(val context: Context) {
    fun changeNfcEnabled( enabled: Boolean): Boolean {
        // Turn NFC on/off
        val mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
                ?: // NFC is not supported
                return false
        object : Thread("toggleNFC") {
            override fun run() {
                var success = false
                val NfcManagerClass: Class<*>
                val setNfcEnabled: Method
                val setNfcDisabled: Method
                val nfc: Boolean = false
                if (enabled) {
                    try {
                        NfcManagerClass = Class.forName(mNfcAdapter.javaClass.name)
                        setNfcEnabled = NfcManagerClass.getDeclaredMethod("enable")
                        setNfcEnabled.isAccessible = true
                        setNfcEnabled.invoke(mNfcAdapter)
                        success = nfc
                    } catch (e: ClassNotFoundException) {
                    } catch (e: NoSuchMethodException) {
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalAccessException) {
                    } catch (e: InvocationTargetException) {
                    }
                } else {
                    try {
                        NfcManagerClass = Class.forName(mNfcAdapter.javaClass.name)
                        setNfcDisabled = NfcManagerClass.getDeclaredMethod("disable")
                        setNfcDisabled.setAccessible(true)
                        setNfcDisabled.invoke(mNfcAdapter)
                        success = nfc
                    } catch (e: ClassNotFoundException) {
                    } catch (e: NoSuchMethodException) {
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalAccessException) {
                    } catch (e: InvocationTargetException) {
                    }
                }
                if (success) {
                    Log.d("QuickSetting", " NFC is enabled")
                } else {
                    Log.d("QuickSetting", " NFC is disabed")
                }
            }
        }.start()
        return false
    } //end method

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