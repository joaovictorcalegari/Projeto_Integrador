package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_monitorar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitorar)
        val botaoVoltar = findViewById<ImageButton>(R.id.btnBack)
        botaoVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Volta para a activity anterior
        }
        }
}