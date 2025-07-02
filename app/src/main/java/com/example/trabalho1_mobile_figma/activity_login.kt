package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class activity_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonCriarConta = findViewById<Button>(R.id.button)
        buttonCriarConta.setOnClickListener {
            startActivity(Intent(this, activity_criar_conta::class.java))
        }
        val botaoEsqueciSenha = findViewById<Button>(R.id.vForgotPasswordt)
        botaoEsqueciSenha.setOnClickListener {
            val intent = Intent(this, activity_recuperar_senha::class.java)
            startActivity(intent)
        }
        val btnLogin = findViewById<Button>(R.id.btnEnter) // botão de login
        btnLogin.setOnClickListener {
            // Aqui você faz a verificação de login, se houver
            val intent = Intent(this, activity_menu_principal::class.java)
            startActivity(intent)
            finish() // remove a tela de login do histórico (opcional)
        }
    }
}

