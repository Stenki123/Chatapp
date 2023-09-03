package com.example.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.activity.ChatActivity
import com.example.chatapp.model.User
import com.example.chatapp.utility.Image
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val userList: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.userName

        holder.imgUser.setImageResource(R.drawable.profile_image)
        Image.load(user.profileImage){
            if(it!=null) holder.imgUser.setImageBitmap(it)
        }

        holder.layoutUser.setOnClickListener{
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("userId",user.userId)
            context.startActivity(intent)
        }
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtUserName: TextView = view.findViewById(R.id.userName)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
        val layoutUser: LinearLayout = view.findViewById(R.id.layoutUser)

    }
}