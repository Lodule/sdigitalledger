package com.sdigitalledger.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import java.io.File

object SecurityHelper {

    /**
     * Detects if the app is running on a rooted device (common penetration test check).
     */
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Checks if the app is currently attached to an active debugger.
     */
    fun isDebuggerAttached(context: Context): Boolean {
        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return android.os.Debug.isDebuggerConnected() || isDebuggable
    }

    /**
     * Sanitizes inputs for SQL / Room queries to prevent injection vulnerabilities.
     */
    fun sanitizeInput(input: String): String {
        return input.replace("'", "''").trim()
    }
}
