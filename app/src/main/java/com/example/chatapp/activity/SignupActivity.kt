package com.example.chatapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignup.setOnClickListener{
            val name = binding.inputName.text.toString()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val confirm_pass = binding.inputConfirmPass.text.toString()
            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_pass.isEmpty()){
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (password != confirm_pass){
                Toast.makeText(this, "passwords need to match", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            registerUser(name,email,password)
        }

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/")

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun registerUser(userName:String,email:String,password:String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid



                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userId", userId)
                    hashMap.put("userName", userName)
                    hashMap.put("profileImage", "")

                    val ref = database.getReference("Users").child(userId)

                    ref.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            //open home activity
                            binding.inputName.setText("")
                            binding.inputEmail.setText("")
                            binding.inputPassword.setText("")
                            binding.inputConfirmPass.setText("")
                            val intent = Intent(this, UsersActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                        else{
                            Toast.makeText(this, "Authentication failed. ${it.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Authentication failed. ${it.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}