package com.example.trabalho1_mobile_figma

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class activity_monitorar : AppCompatActivity() {

    private lateinit var contagem: TextView
    private lateinit var btnEsteira: Button
    private lateinit var controleRef: DatabaseReference
    private var esteiraLigada = false
    private lateinit var btnZerarContagem: Button
    private lateinit var tvStatusConexao: TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitorar)
        btnZerarContagem = findViewById(R.id.btnZerarContagem)

        tvStatusConexao = findViewById(R.id.tvStatusConexao)
        escutarStatusConexao()

        btnZerarContagem.setOnClickListener {
            val countRef = FirebaseDatabase.getInstance().getReference("tampas/count")
            countRef.setValue(0).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Contagem zerada com sucesso.")
                } else {
                    Log.e("Firebase", "Erro ao zerar contagem: ${task.exception?.message}")
                }
            }
        }
        // Inicializa elementos da UI
        contagem = findViewById(R.id.contagem)
        btnEsteira = findViewById(R.id.btnEsteira)
        val botaoVoltar = findViewById<ImageButton>(R.id.btnBack)

        // Referência do botão ON/OFF no Firebase
        controleRef = FirebaseDatabase.getInstance().getReference("controle/iniciarEsteira")

        // Escuta os dados da contagem e do botão
        escutarContagem()
        escutarStatusEsteira()

        // Botão de voltar
        botaoVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Botão ON/OFF da esteira
        btnEsteira.setOnClickListener {
            val novoEstado = !esteiraLigada
            btnEsteira.isEnabled = false  // desabilita temporariamente

            controleRef.setValue(novoEstado).addOnCompleteListener {
                btnEsteira.isEnabled = true  // reabilita depois do envio
            }
        }
    }

    private fun escutarStatusConexao() {
        val statusRef = FirebaseDatabase.getInstance().getReference("status/wifiConectado")
        statusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val conectado = snapshot.getValue(Boolean::class.java) ?: false
                if (conectado) {
                    tvStatusConexao.text = "Conectado"
                    tvStatusConexao.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                } else {
                    tvStatusConexao.text = "Sem conexão"
                    tvStatusConexao.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                tvStatusConexao.text = "Erro ao verificar conexão"
            }
        })
    }

    private fun escutarContagem() {
        val referencia = FirebaseDatabase.getInstance().getReference("tampas/count")
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val valor = snapshot.getValue(Int::class.java)
                contagem.text = "Contagem: ${valor ?: 0}"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erro ao ler contagem: ${error.message}")
            }
        })
    }

    private fun escutarStatusEsteira() {
        controleRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                esteiraLigada = snapshot.getValue(Boolean::class.java) ?: false
                btnEsteira.text = if (esteiraLigada) "Parar Esteira" else "Iniciar Esteira"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erro ao ler status da esteira: ${error.message}")
            }
        })
    }
}