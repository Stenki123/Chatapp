package com.example.chatapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapter.ChatAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.Chat
import com.example.chatapp.model.User
import com.example.chatapp.utility.DB
import com.example.chatapp.utility.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    private lateinit var binding: ActivityChatBinding
    var chatList = ArrayList<Chat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatrecview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        var intent = getIntent()
        var userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(userId!!)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)
                binding.tvUserName.text = user!!.userName
                binding.imgProfile.setImageBitmap(null)

            }
        })

        binding.btnSendMessage.setOnClickListener{
            var message:String = binding.etMessage.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
                binding.etMessage.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                binding.etMessage.setText("")
                }
            }

        readMessage(firebaseUser!!.uid, userId)

    }

    private fun sendMessage(senderId:String,receiverId:String,message:String){
        val reference: DatabaseReference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
        val hashMap: HashMap<String, String> = HashMap()

        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)

        reference!!.child("Chat").push().setValue(hashMap)
    }

    fun readMessage(senderId: String, receiverId: String) {
        var reference: DatabaseReference = Firebase.database("https://chatapp2-eb260-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chat")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId == senderId && chat!!.receiverId == receiverId ||
                        chat!!.senderId == receiverId && chat!!.receiverId == senderId
                    ) {
                        chatList.add(chat)
                    }
                }


                if(chatList.isNotEmpty()){
                    val chat=chatList.first()
                    val leftUserId=if (chat.receiverId==firebaseUser!!.uid)chat.senderId else chat.receiverId
                    DB.readUser(leftUserId) {leftUser->
                        DB.readUser(firebaseUser!!.uid) {rightUser->
                            Image.load(leftUser.profileImage) {leftImage->
                                Image.load(rightUser.profileImage){rightImage->
                                    val chatAdapter = ChatAdapter(this@ChatActivity, chatList, leftImage,rightImage)
                                    binding.chatrecview.adapter = chatAdapter
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}

