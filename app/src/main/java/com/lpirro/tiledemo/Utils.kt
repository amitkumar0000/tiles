package com.lpirro.tiledemo

import android.R
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.DataOutputStream
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

object Utils {

    val QSETTING_CONFIG = "QSETTING_CONFIG"

    var disableStatusBar = true
        set(value) {
            field = value
        }

    fun setTint(view: ImageView, color: Int) {
        DrawableCompat.setTint(
                DrawableCompat.wrap(view.drawable),
                ContextCompat.getColor(view.context, color)
        )
    }

    fun grantNotificationAccess() {
        val p = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(p.outputStream)
        os.writeBytes("pm grant com.lpirro.tiledemo android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" + "\n")
        os.writeBytes("exit\n")
        os.flush()
    }

    fun executeCommandViaSu(option: String, command: String, statelistener: (Boolean) -> Unit, enableOrDisable: Boolean) {
        var success = false
        var su = "su"
        for (i in 0..2) {
            // Default "su" command executed successfully, then quit.
            if (success) {
                break
            }
            // Else, execute other "su" commands.
            if (i == 1) {
                su = "/system/xbin/su"
            } else if (i == 2) {
                su = "/system/bin/su"
            }
            try {
                // Execute command as "su".
                Runtime.getRuntime().exec(arrayOf(su, option, command))
            } catch (e: IOException) {
                success = false
                // Oops! Cannot execute `su` for some reason.
                // Log error here.
                statelistener(false)
            } finally {
                success = true
                statelistener(enableOrDisable)
            }
        }
    }

    fun disableStatusBar(context: Context) {
        if(disableStatusBar) {
            closeStatusBar(context)
        }
    }

    fun closeStatusBar(context: Context) {
        Observable.timer(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    collapse(context)
                    disableStatusBar(context)
                },{})
    }

    private fun collapse(context: Context) {
        // Use reflection to trigger a method from 'StatusBarManager'
        val statusBarService = context.getSystemService("statusbar")
        var statusBarManager: Class<*>? = null
        try {
            statusBarManager = Class.forName("android.app.StatusBarManager")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        var collapseStatusBar: Method? = null
        try {

            // Prior to API 17, the method to call is 'collapse()'
            // API 17 onwards, the method to call is `collapsePanels()`
            collapseStatusBar = if (Build.VERSION.SDK_INT > 16) {
                statusBarManager!!.getMethod("collapsePanels")
            } else {
                statusBarManager!!.getMethod("collapse")
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        collapseStatusBar?.isAccessible = true
        try {
            collapseStatusBar?.invoke(statusBarService)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}