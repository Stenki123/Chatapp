package com.example.chatapp.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityProfileBinding
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST: Int = 2020


    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val databaseReference: DatabaseReference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etuserName.setText(user!!.userName)
                binding.userImage.setImageResource(R.drawable.profile_image)

                Image.load(user.profileImage){
                    if(it!=null) binding.userImage.setImageBitmap(it)
                }
            }
        })

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.userImage.setOnClickListener {
            chooseImage()
        }

        binding.btnSave.setOnClickListener {
            uploadImage()
            binding.progressBar.visibility = android.view.View.VISIBLE
        }
    }
    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
            filePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.userImage.setImageBitmap(bitmap)
                binding.btnSave.visibility = android.view.View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage() {
        if (filePath != null) {



            val imageId = UUID.randomUUID().toString()
            var ref: StorageReference = storageRef.child("image/" + imageId)
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                        val hashMap: HashMap<String, String> = HashMap()
                        val databaseReference: DatabaseReference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(firebaseUser.uid)

                        hashMap.put("userName", binding.etuserName.text.toString())
                        hashMap.put("profileImage", imageId)
                        databaseReference.updateChildren(hashMap as Map<String, Any>)

                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                        binding.btnSave.visibility = android.view.View.GONE

                }
                .addOnFailureListener {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(applicationContext, "Failed$it", Toast.LENGTH_SHORT).show()

                }


        }
    }
}
