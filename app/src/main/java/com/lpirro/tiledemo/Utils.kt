package com.lpirro.tiledemo

import android.R
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.io.DataOutputStream
import java.io.IOException

object Utils {
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

}