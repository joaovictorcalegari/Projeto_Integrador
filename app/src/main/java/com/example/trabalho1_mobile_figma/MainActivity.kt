package com.example.trabalho1_mobile_figma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper
import android.os.Handler
import android.content.Intent
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

    }
}