package com.example.crudtestff

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val button:Button=findViewById(R.id.button4)

        button.setOnClickListener {
            val intent = Intent(this@Home, MainActivity::class.java)
            startActivity(intent)
        }
        }
    }
