package com.example.chatmessenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chatmessenger.R
import com.example.chatmessenger.chat_message.ChatMessage
import com.example.chatmessenger.chat_message.ChatMessageAdapter
import com.example.chatmessenger.chat_user.ChatUser
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment() {

    interface OnSelectedButtonListener {
        fun onButtonSelected(userID: String, messageStr: String)
    }

    private lateinit var messageTextView :TextView
    private lateinit var sendButton :MaterialButton
    private var messages = ArrayList<ChatMessage>()
    private var messagesList: ListView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        messageTextView = rootView.findViewById(R.id.message_text)
        messageTextView.text = ""

        sendButton = rootView.findViewById(R.id.sendChatButton)

        messages = ArrayList()
        setInitialData()

        messagesList = rootView.findViewById(R.id.chat_field)
        val chatMessageAdapter = ChatMessageAdapter(this.context, R.layout.chat_message_list, messages)
        messagesList?.adapter = chatMessageAdapter

        sendButton.setOnClickListener {
            val gsonUser = Gson()
            val currentUserFile = File(context?.filesDir?.path , "currentUser.json")
            val user = gsonUser.fromJson(FileReader(currentUserFile), ChatUser::class.java)
            val message = messageTextView.text.toString()
            val calendarTime = Calendar.getInstance().time
            val formatter = SimpleDateFormat("dd.MM.yy HH:mm")
            val time = formatter.format(calendarTime).toString()
            messages.add(ChatMessage(user.getLogin(), message, time))
            chatMessageAdapter.notifyDataSetChanged()

            val gsonMessages = GsonBuilder().setPrettyPrinting().create()
            val jsonMessages: String = gsonMessages.toJson(messages)
            val messagesFile = File(context?.filesDir?.path, "messages.json")
            if (!messagesFile.exists()) {
                messagesFile.createNewFile()
            }
            messagesFile.writeText(jsonMessages)

            val listener = activity as OnSelectedButtonListener
            listener.onButtonSelected(user.getNickname(), message)
            messageTextView.text = ""
        }

        return rootView
    }

    private fun setInitialData() {
//        val chatsLogin = resources.getStringArray(R.array.Users_list)
//        val chatsMessage = resources.getStringArray(R.array.Phone_list)
//        val messagesTime = resources.getStringArray(R.array.Emails_list)
//        val chatsPhoto = ArrayList<Int>()
//        for (index in chatsLogin.indices) {
//            val chatLogin = chatsLogin[index].toString().lowercase()
//            val photoId = resources.getIdentifier(chatLogin, "drawable", requireContext().packageName)
//            chatsPhoto.add(photoId)
//        }
//        messages.add(ChatMessage(chatsLogin[3], chatsPhoto[3], "Hi Roman. How are you?", "07.02.23 14:05"))
//        messages.add(ChatMessage(chatsLogin[2], chatsPhoto[2], "Hi Dad. I'm fine. Thank you. And how are You.", "07.02.23 14:11"))
//        messages.add(ChatMessage(chatsLogin[3], chatsPhoto[3], "I'm Ok. Do you come to us today?", "07.02.23 14:13"))
//        messages.add(ChatMessage(chatsLogin[3], chatsPhoto[3], "We are going to cover the dinner table.", "07.02.23 14:14"))
//        messages.add(ChatMessage(chatsLogin[3], chatsPhoto[3], "Melissa is also coming for the holiday.", "07.02.23 14:14"))
//        messages.add(ChatMessage(chatsLogin[2], chatsPhoto[2], "I can't promise You. I will try.", "07.02.23 14:15"))
        val gson = Gson()
        val arrayListChatMessageType = object : TypeToken<ArrayList<ChatMessage>>() {}.type
        val file = File(context?.filesDir?.path, "messages.json")
        if (file.exists()) {
            messages = gson.fromJson(FileReader(file), arrayListChatMessageType)
        }
    }

}