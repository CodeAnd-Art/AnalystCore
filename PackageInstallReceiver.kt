package com.moby.antivirus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageInstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED) {
            val packageName = intent.data?.schemeSpecificPart ?: return
            val scanIntent = Intent(context, ScannerService::class.java)
            scanIntent.action = "SCAN_PACKAGE"
            scanIntent.putExtra("package_name", packageName)
            context.startService(scanIntent)
        }
    }
}