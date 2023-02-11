package com.example.chatmessenger.chat_user

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.chatmessenger.R
import java.io.File


class ChatUserAdapter(context: Context?, private val layout: Int, private val users: List<ChatUser>)
    :ArrayAdapter<ChatUser?>(context!!, layout, users) {

    private val TAG = "ChatUserAdapter"
    private val inflater: LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(layout, parent, false)
        val pictureView = view.findViewById<ImageView>(R.id.user_photo)
        val nicknameView = view.findViewById<TextView>(R.id.user_nickname)
        val phoneView = view.findViewById<TextView>(R.id.user_phone)
        val emailView = view.findViewById<TextView>(R.id.user_email)
        val user = users[position]
        val defaultPhoto = "blank_profile_picture_120"
        val fileName = user.getLogin().lowercase()
        val imgFile = File(context.filesDir.path, "$fileName.jpg")
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            pictureView.setImageBitmap(myBitmap)
        } else if (user.getPhotoResource() > 0) {
            pictureView.setImageResource(user.getPhotoResource())
        } else {
            pictureView.setImageResource(context.resources
                .getIdentifier(defaultPhoto, "drawable", context.packageName))
        }
        nicknameView.text = user.getNickname()
        phoneView.text = user.getPhone()
        emailView.text = user.getEmail()

        return view
    }

    init {
        inflater = LayoutInflater.from(context)
    }

}