package com.example.chatapp.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object Image {
    private val storageRef = FirebaseStorage.getInstance().reference
    fun load (id:String, callback:(Bitmap?)-> Unit){
        val ref: StorageReference = storageRef.child("image/$id")
        ref.getBytes(128*1024*1024).addOnCompleteListener {
            if(it.isSuccessful){
                val data = it.result
                val bitmap = BitmapFactory.decodeByteArray(data,0,data.size)
                callback(bitmap)
            }else{
                callback(null)
            }
        }
    }
}