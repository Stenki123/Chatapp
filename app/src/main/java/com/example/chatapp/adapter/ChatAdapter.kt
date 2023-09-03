package com.example.chatapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context, private val chatList: List<Chat>, private val leftImage: Bitmap?, private val rightImage: Bitmap?) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    private val MESSAGE_TYPE_LEFT=0
    private val MESSAGE_TYPE_RIGHT=1
    var firebaseUser: FirebaseUser? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)

        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtUserName.text = chat.message
        holder.imgUser.setImageResource(R.drawable.profile_image)

        if(getItemViewType(position)==MESSAGE_TYPE_LEFT){

            leftImage?.let {
                holder.imgUser.setImageBitmap(it)
            }
        }else{
            rightImage?.let {
                holder.imgUser.setImageBitmap(it)
            }
        }
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].senderId == firebaseUser!!.uid){
            return MESSAGE_TYPE_RIGHT
        }else{
            return MESSAGE_TYPE_LEFT
        }
    }

}