package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class activity_criar_conta : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnCreate: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        // Inicializa o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Referências aos elementos do layout
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnCreate = findViewById(R.id.btnCreate)

        // Botão "Criar conta"
        btnCreate.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val senha = etPassword.text.toString().trim()

            if (email.isNotEmpty() && senha.length >= 6) {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, activity_menu_principal::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha os campos corretamente (senha com 6+ caracteres)", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão "Já tem conta? Fazer login"
        val botaoVoltarLogin = findViewById<Button>(R.id.tvDoLogin)
        botaoVoltarLogin.setOnClickListener {
            startActivity(Intent(this, activity_login::class.java))
            finish()
        }
    }
}
