package com.moby.antivirus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moby.antivirus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnQuickScan.setOnClickListener {
            val intent = Intent(this, ScannerService::class.java)
            intent.action = "SCAN_QUICK"
            startService(intent)
        }

        binding.btnFullScan.setOnClickListener {
            val intent = Intent(this, ScannerService::class.java)
            intent.action = "SCAN_FULL"
            startService(intent)
        }

        binding.btnViewLastScan.setOnClickListener {
            // En son sonucu açmak için boş bir activity (ileride cache'leyebilirsin)
            startActivity(Intent(this, ScanResultActivity::class.java))
        }
    }
}