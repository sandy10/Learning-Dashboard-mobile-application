package com.learning.dashboardmobileapp.core.data.security

import android.os.Build
import android.view.Window
import android.view.WindowManager
import java.io.File

/**
 * Utility functions for device integrity, screen protection, and application security.
 */
object SecurityUtils {

    /**
     * Secures the target activity window against screen capture, screen recording,
     * and recent tasks thumbnail snapshot leaks in the OS task switcher.
     * Set enabled = true for hardened release builds; set enabled = false for demo video recording.
     */
    fun enableSecureWindow(window: Window, enabled: Boolean = false) {
        if (enabled) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    /**
     * Inspects the host device environment for root indicators, test-keys signatures,
     * and known privilege-escalation binaries.
     */
    fun isDeviceRooted(): Boolean {
        return checkBuildTags() || checkCommonRootBinaries() || checkSuBinaryExecutable()
    }

    private fun checkBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkCommonRootBinaries(): Boolean {
        val knownPaths = arrayOf(
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
        return knownPaths.any { path ->
            try {
                File(path).exists()
            } catch (_: SecurityException) {
                false
            }
        }
    }

    private fun checkSuBinaryExecutable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (_: Exception) {
            false
        }
    }
}
