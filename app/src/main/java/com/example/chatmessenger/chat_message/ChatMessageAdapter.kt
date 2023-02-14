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
import com.example.chatmessenger.database.DBHelper
import java.io.File


class ChatMessageAdapter(context: Context?, private val layout: Int, private val messages: List<ChatMessage>)
    :ArrayAdapter<ChatMessage?>(context!!, layout, messages) {

    private val inflater: LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(layout, parent, false)
        val pictureView = view.findViewById<ImageView>(R.id.chat_photo)
        val nicknameView = view.findViewById<TextView>(R.id.chat_nickname)
        val messageView = view.findViewById<TextView>(R.id.chat_message)
        val timeView = view.findViewById<TextView>(R.id.message_time)
        val message = messages[position]

        val dBHelper: DBHelper = DBHelper.getInstance(context)
        val users = dBHelper.allUsers

        var user : ChatUser? = null
        for (i in users.indices) {
            if (users[i].getLogin() == message.getLogin()) {
                user = users[i]
            }
        }

        val defaultPhoto = "blank_profile_picture_120"
        val fileName = user?.getLogin()?.lowercase()
        val drawableIdentifier = context.resources.getIdentifier(fileName, "drawable", context.packageName)
        val imgFile = File(context.filesDir.path, "$fileName.jpg")
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            pictureView.setImageBitmap(myBitmap)
        } else if (drawableIdentifier > 0) {
            pictureView.setImageResource(drawableIdentifier)
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