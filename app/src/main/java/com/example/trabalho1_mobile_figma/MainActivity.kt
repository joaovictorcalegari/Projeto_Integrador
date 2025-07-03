

package com.example.trabalho1_mobile_figma

// Importações necessárias para usar o Firebase Realtime Database
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.*

//Tela do splash
class MainActivity : AppCompatActivity() {


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