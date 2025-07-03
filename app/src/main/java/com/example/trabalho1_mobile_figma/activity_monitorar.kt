package com.example.trabalho1_mobile_figma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*
import android.util.Log

class activity_monitorar : AppCompatActivity() {
    private lateinit var contagem : TextView
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitorar)
        contagem = findViewById(R.id.contagem)
        escutarFirebase()

        //botao voltar
        val botaoVoltar = findViewById<ImageButton>(R.id.btnBack)
        botaoVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Volta para a activity anterior
        }


    }

    private fun escutarFirebase() {
        val database = FirebaseDatabase.getInstance()
        val referencia = database.getReference("tampas/count")

        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val valor = snapshot.getValue(Int::class.java)
                contagem.text = "Contagem: ${valor ?: 0}"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erro ao ler dados: ${error.message}")
            }
        })
    }
}