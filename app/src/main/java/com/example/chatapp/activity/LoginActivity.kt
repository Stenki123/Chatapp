package com.example.chatapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        auth.currentUser?.let {
            val intent = Intent(
                this@LoginActivity,
                UsersActivity::class.java
            )
            startActivity(intent)
            finish()
        }


        binding.btnLogin.setOnClickListener{
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                    if (it.isSuccessful){
                        binding.inputEmail.setText("")
                        binding.inputPassword.setText("")
                        val intent = Intent(this, UsersActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        binding.btnSignup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}