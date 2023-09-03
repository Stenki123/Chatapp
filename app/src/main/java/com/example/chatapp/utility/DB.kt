package com.example.chatapp.utility

import com.example.chatapp.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DB {
    fun readUser(id:String,callback:(User)->Unit){
        val databaseReference: DatabaseReference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(id)
        databaseReference.get().addOnSuccessListener{
            it.getValue(User::class.java)?.let(callback)
        }
    }
}