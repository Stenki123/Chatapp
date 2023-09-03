package com.example.chatapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.ActivityUsersBinding
import com.example.chatapp.model.User
import com.example.chatapp.utility.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class UsersActivity : AppCompatActivity() {
    var userList = ArrayList<User>()
    private lateinit var binding: ActivityUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.imgBack.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imgProfile.setOnClickListener {
            val intent = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        getUsersList()

    }
    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")


        val databaseReference: DatabaseReference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                binding.imgProfile.setImageResource(R.drawable.profile_image)

                var currentUser:User?=null
                for (dataSnapShot: DataSnapshot in snapshot.children) {

                    val user = dataSnapShot.getValue(User::class.java) ?: continue

                    if (user.userId != firebase.uid) {

                        userList.add(user)
                    }else{
                        currentUser=user
                    }

                }

                currentUser?.let {
                    Image.load(it.profileImage){
                        if(it!=null) binding.imgProfile.setImageBitmap(it)
                    }
                }
                val userAdapter = UserAdapter(this@UsersActivity, userList)

                binding.recview.adapter = userAdapter
            }

        }
        )
}
}