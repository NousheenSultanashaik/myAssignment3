package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment3.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //this is the code for the eye button when i click on it it will display the password else it will hide the password
        binding.ibEyeLogin.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            val transformationMethod =
                if (isPasswordVisible) null else PasswordTransformationMethod()
            binding.passET.transformationMethod = transformationMethod
            binding.ibEyeLogin.setImageResource(
                if (isPasswordVisible) R.drawable.baseline_remove_red_eye_24 else R.drawable.baseline_remove_red_eye_24
            )
        }
        binding.Back.setOnClickListener {
            onBackPressed()
        }
        //here i have used the firebase authentication for getting the instance
        firebaseAuth = FirebaseAuth.getInstance()
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, OtherActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this,"Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}