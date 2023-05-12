package com.example.assignment3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //here i have 2 buttons one is login and other is signup
        //when i click on login it should take me to login activity
            //when i click on signup it should take me to the signup activity
        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val  signButton: Button = findViewById(R.id.signup_button)
        signButton.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}