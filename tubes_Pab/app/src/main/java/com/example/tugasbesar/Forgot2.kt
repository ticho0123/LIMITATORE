package com.example.tugasbesar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tugasbesar.databinding.ActivityForgot2Binding

class Forgot2 : AppCompatActivity() {

    private lateinit var binding: ActivityForgot2Binding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgot2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackSignIn.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }
    }
}