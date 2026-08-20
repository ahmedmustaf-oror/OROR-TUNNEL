package com.example.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object SecurityManager {

    fun sanitizePayload(rawPayload: String): String {
        if (rawPayload.isEmpty()) return ""
        // Sanitize dangerous non-HTTP injection characters while preserving standard SSH/VPN payload tags
        return rawPayload.replace("<script>", "")
            .replace("</script>", "")
            .trim()
    }

    fun isAppTampered(context: Context): Boolean {
        // Verify package integrity
        val expectedPackage = "com.oror.tunnel"
        val currentPackage = context.packageName
        return currentPackage != expectedPackage && currentPackage != "com.example"
    }

    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}"
    }

    fun getAndroidVersion(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }
}
