package com.moby.antivirus

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScannerService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = buildNotification("Tarama başlıyor...")
        startForeground(1, notification)

        val scanner = Scanner(applicationContext)
        MalwareDatabase.loadSignatures(applicationContext)

        when (intent?.action) {
            "SCAN_FULL" -> {
                val results = scanner.fullScan()
                showResults(results)
            }
            "SCAN_QUICK" -> {
                val results = scanner.quickScan()
                showResults(results)
            }
            "SCAN_PACKAGE" -> {
                val pkg = intent.getStringExtra("package_name") ?: return START_NOT_STICKY
                val result = scanner.scanSinglePackage(pkg)
                if (result != null && result.isThreat) {
                    sendThreatNotification(result)
                }
            }
        }
        stopForeground(true)
        stopSelf()
        return START_NOT_STICKY
    }

    private fun showResults(results: List<ScanResult>) {
        val intent = Intent(this, ScanResultActivity::class.java).apply {
            putParcelableArrayListExtra("results", ArrayList(results))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun sendThreatNotification(result: ScanResult) {
        val notification = NotificationCompat.Builder(this, "moby_scan")
            .setContentTitle("Tehdit bulundu!")
            .setContentText("${result.appName} zararlı olabilir. Durum: ${result.status}")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(2, notification)
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "moby_scan")
            .setContentTitle("Moby Antivirüs")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "moby_scan",
                "Tarama Bildirimleri",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}