package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_menu_principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        val btnIrMonitoramento = findViewById<ImageButton>(R.id.btnIrMonitoramento)
        btnIrMonitoramento.setOnClickListener {
            val intent = Intent(this, activity_monitorar::class.java)
            startActivity(intent)
        }
    }
}