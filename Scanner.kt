package com.moby.antivirus

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.io.File
import java.security.MessageDigest

data class ScanResult(
    val appName: String,
    val packageName: String,
    val icon: Drawable?,
    val status: String, // "Temiz", "Zararlı (imza)", "Şüpheli (izin + yan yükleme)"
    val isThreat: Boolean
)

class Scanner(private val context: Context) {
    private val pm = context.packageManager
    private val malwareDb = MalwareDatabase

    fun fullScan(): List<ScanResult> {
        val results = mutableListOf<ScanResult>()
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in installed) {
            if (app.packageName == context.packageName) continue // kendini tarama
            results.add(analyzeApp(app))
        }
        return results
    }

    fun quickScan(): List<ScanResult> {
        val results = mutableListOf<ScanResult>()
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        // Sadece son 24 saatte değişen APK'lar veya yan yükleme şüphesi olanlar
        for (app in installed) {
            if (app.packageName == context.packageName) continue
            val apkFile = File(app.sourceDir)
            if (apkFile.lastModified() > System.currentTimeMillis() - 86400000) {
                results.add(analyzeApp(app))
            }
        }
        return results
    }

    fun scanSinglePackage(packageName: String): ScanResult? {
        try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            return analyzeApp(appInfo)
        } catch (e: Exception) {
            return null
        }
    }

    private fun analyzeApp(app: ApplicationInfo): ScanResult {
        val appName = app.loadLabel(pm).toString()
        val icon = app.loadIcon(pm)
        val apkPath = app.sourceDir
        val hash = calculateSHA256(File(apkPath))
        val installer = pm.getInstallerPackageName(app.packageName)
        val isSideLoaded = installer != "com.android.vending"

        // 1. İmza kontrolü
        if (malwareDb.checkHash(hash)) {
            return ScanResult(appName, app.packageName, icon, "Zararlı (imza eşleşti)", true)
        }

        // 2. Şüpheli izin + yan yükleme kontrolü
        val permissions = getPackagePermissions(app.packageName)
        val dangerousPerms = listOf(
            "android.permission.RECEIVE_SMS",
            "android.permission.SEND_SMS",
            "android.permission.READ_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECORD_AUDIO",
            "android.permission.CAMERA",
            "android.permission.READ_CALL_LOG"
        )
        val riskyPerms = permissions.filter { it in dangerousPerms }
        if (isSideLoaded && riskyPerms.size >= 3) {
            return ScanResult(appName, app.packageName, icon, "Şüpheli (3+ tehlikeli izin, Play Store dışı)", true)
        }

        // 3. Temiz
        return ScanResult(appName, app.packageName, icon, "Temiz", false)
    }

    private fun getPackagePermissions(packageName: String): List<String> {
        return try {
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            packageInfo.requestedPermissions?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun calculateSHA256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}