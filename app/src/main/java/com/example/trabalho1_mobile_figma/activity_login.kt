package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class activity_login : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEnter: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializa os componentes
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnEnter = findViewById(R.id.btnEnter)
        val buttonCriarConta = findViewById<Button>(R.id.button)
        val botaoEsqueciSenha = findViewById<Button>(R.id.vForgotPasswordt)

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Login
        btnEnter.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val senha = etPassword.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, activity_menu_principal::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ir para tela de cadastro
        buttonCriarConta.setOnClickListener {
            startActivity(Intent(this, activity_criar_conta::class.java))
        }

        // Ir para tela de recuperação de senha
        botaoEsqueciSenha.setOnClickListener {
            startActivity(Intent(this, activity_recuperar_senha::class.java))
        }
    }
}
