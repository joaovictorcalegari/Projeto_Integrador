package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class activity_recuperar_senha : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSendLink: Button
    private lateinit var btnBack: ImageButton
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Referências da interface
        etEmail = findViewById(R.id.etRecoveryEmail)
        btnSendLink = findViewById(R.id.btnSendLink)
        btnBack = findViewById(R.id.btnBack)

        // Botão de enviar link
        btnSendLink.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "E-mail de recuperação enviado!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, activity_login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão voltar
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
