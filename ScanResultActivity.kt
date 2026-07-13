package com.moby.antivirus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScanResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val tvStatus = findViewById<android.widget.TextView>(R.id.tvScanStatus)

        val results = intent.getParcelableArrayListExtra<ScanResult>("results") ?: emptyList()
        if (results.isEmpty()) {
            tvStatus.text = "Tehdit bulunamadı ✅"
        } else {
            val threats = results.count { it.isThreat }
            tvStatus.text = "Tarama tamamlandı. $threats tehdit bulundu."
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ScanResultAdapter(results)
    }
}