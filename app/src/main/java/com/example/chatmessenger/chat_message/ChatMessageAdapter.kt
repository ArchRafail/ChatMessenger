package com.example.chatmessenger.chat_message

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.chatmessenger.R
import com.example.chatmessenger.chat_user.ChatUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader


class ChatMessageAdapter(context: Context?, private val layout: Int, private val messages: List<ChatMessage>)
    :ArrayAdapter<ChatMessage?>(context!!, layout, messages) {

    private val TAG = "ChatMessageAdapter"
    private val inflater: LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(layout, parent, false)
        val pictureView = view.findViewById<ImageView>(R.id.chat_photo)
        val nicknameView = view.findViewById<TextView>(R.id.chat_nickname)
        val messageView = view.findViewById<TextView>(R.id.chat_message)
        val timeView = view.findViewById<TextView>(R.id.message_time)
        val message = messages[position]

        val gson = Gson()
        val arrayListChatUserType = object : TypeToken<ArrayList<ChatUser>>() {}.type
        val file = File(context.filesDir.path, "users.json")
        var users :ArrayList<ChatUser> = ArrayList()
        if (file.exists()) {
            users = gson.fromJson(FileReader(file), arrayListChatUserType)
        }
        var user : ChatUser? = null
        for (i in users.indices) {
            if (users[i].getLogin() == message.getLogin()) {
                user = users[i]
            }
        }

        val defaultPhoto = "blank_profile_picture_120"
        val fileName = user?.getLogin()?.lowercase()
        val imgFile = File(context.filesDir.path, "$fileName.jpg")
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            pictureView.setImageBitmap(myBitmap)
        } else if (user?.getPhotoResource()!! > 0) {
            pictureView.setImageResource(user.getPhotoResource())
        } else {
            pictureView.setImageResource(context.resources
                .getIdentifier(defaultPhoto, "drawable", context.packageName))
        }
        nicknameView.text = user?.getNickname()
        messageView.text = message.getMessage()
        timeView.text = message.getTime()

        return view
    }

    init {
        inflater = LayoutInflater.from(context)
    }

}