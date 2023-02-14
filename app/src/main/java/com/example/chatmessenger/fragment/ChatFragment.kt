package com.example.chatmessenger.fragment

import android.content.SharedPreferences
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
import com.example.chatmessenger.database.DBHelper
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {

    interface OnSelectedButtonListener {
        fun onButtonSelected(userID: String, messageStr: String)
    }

    private lateinit var messageTextView :TextView
    private lateinit var sendButton :MaterialButton
    private var messages = ArrayList<ChatMessage>()
    private var messagesList: ListView? = null
    private var dBHelper: DBHelper? = null

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
        dBHelper = DBHelper.getInstance(context)
        if (dBHelper != null) {
            messages = dBHelper!!.allMessages
        }

        messagesList = rootView.findViewById(R.id.chat_field)
        val chatMessageAdapter = ChatMessageAdapter(this.context, R.layout.chat_message_list, messages)
        messagesList?.adapter = chatMessageAdapter
        scrollMyListViewToBottom(chatMessageAdapter)

        sendButton.setOnClickListener {

            val myPreferences : SharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(requireContext())
            val login = myPreferences.getString("Login", "NoName")

            val message = messageTextView.text.toString()
            val calendarTime = Calendar.getInstance().time
            val formatter = SimpleDateFormat("dd.MM.yy HH:mm")
            val time = formatter.format(calendarTime).toString()
            messages = ArrayList()
            dBHelper!!.addMessage(login, message, time)
            messages = dBHelper!!.allMessages
            val updatedChatMessageAdapter = ChatMessageAdapter(this.context, R.layout.chat_message_list, messages)
            messagesList?.adapter = updatedChatMessageAdapter
            scrollMyListViewToBottom(updatedChatMessageAdapter)

            val listener = activity as OnSelectedButtonListener
            listener.onButtonSelected(dBHelper!!.getNicknameByLogin(login), message)
            messageTextView.text = ""
        }

        return rootView
    }

    private fun scrollMyListViewToBottom(adapter: ChatMessageAdapter) {
        messagesList?.post {
            messagesList?.setSelection(adapter.count - 1)
        }
    }

}