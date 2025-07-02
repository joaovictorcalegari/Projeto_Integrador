

package com.example.trabalho1_mobile_figma

// Importações necessárias para usar o Firebase Realtime Database
import com.google.firebase.database.*
import androidx.lifecycle.MutableLiveData
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper
import android.os.Handler
import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.initialize

//Tela do splash
class MainActivity : AppCompatActivity() {

    // Declara a referência do banco de dados
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // LiveData para observar a contagem em tempo real
    private val _contagem = MutableLiveData<Int>()

    // Inicia a escuta da contagem quando o ViewModel é criado
    init {
        escutarContagem()
    }
    private fun escutarContagem() {
        // Caminho no Firebase Realtime Database: "tampas/count"
        val contagemRef = database.child("tampas/count")

        // Adiciona um listener para receber atualizações em tempo real
        contagemRef.addValueEventListener(object : ValueEventListener {

            // Chamada sempre que o valor for alterado no Firebase
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lê o valor inteiro do snapshot (ou usa 0 se for nulo)
                val valor = snapshot.getValue(Int::class.java) ?: 0
                _contagem.value = valor  // Atualiza o LiveData
            }

            // Chamada se ocorrer algum erro na leitura dos dados
            override fun onCancelled(error: DatabaseError) {
                _contagem.value = -1  // Usamos -1 para indicar erro
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(Looper.getMainLooper()).postDelayed({
            val loginIntent = Intent(this, activity_login::class.java)
            startActivity(loginIntent)
            finish()
        }, 2000)


        //Firebase

        val database = Firebase.database
        val myRef = database.getReference("Console:")
        myRef.setValue("FireBase Conectada!")




    }
}