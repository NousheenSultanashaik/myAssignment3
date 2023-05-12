package com.example.assignment3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.assignment3.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var checkbox: CheckBox
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //storing the data in the firebase i have used this firebase instance
        firebaseAuth = FirebaseAuth.getInstance()
        binding.Backsignup.setOnClickListener {
            onBackPressed()
        }
        binding.ibEyeSignup.setOnClickListener{
            isPasswordVisible = !isPasswordVisible
            val transformationMethod = if (isPasswordVisible) null else PasswordTransformationMethod()
            binding.passET.transformationMethod = transformationMethod
            binding.ibEyeSignup.setImageResource(
                if (isPasswordVisible) R.drawable.baseline_remove_red_eye_24 else R.drawable.baseline_remove_red_eye_24
            )


        }
        binding.reenterpassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            val transformationMethod = if (isConfirmPasswordVisible) null else PasswordTransformationMethod()
            binding.confirmPassEt.transformationMethod = transformationMethod
            binding.reenterpassword.setImageResource(
                if (isConfirmPasswordVisible) R.drawable.baseline_remove_red_eye_24 else R.drawable.baseline_remove_red_eye_24
            )
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val user = binding.userEt.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && user.isNotEmpty() )
            {
                if(pass == confirmPass)
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    Toast.makeText(this,"Password is not matching " , Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}