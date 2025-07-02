package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class activity_criar_conta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)
        val botaoVoltarLogin = findViewById<Button>(R.id.tvDoLogin)

        botaoVoltarLogin.setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)
            finish()
        }
        val btnLogin = findViewById<Button>(R.id.btnCreate) // botão de login
        btnLogin.setOnClickListener {
            // Aqui você faz a verificação de login, se houver
            val intent = Intent(this, activity_menu_principal::class.java)
            startActivity(intent)
            finish() // remove a tela de login do histórico (opcional)
        }
    }
}
