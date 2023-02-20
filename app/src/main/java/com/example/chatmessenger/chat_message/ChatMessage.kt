package com.example.chatmessenger.chat_message

import java.io.Serializable

data class ChatMessage(var login: String, var message: String, var time: String) : Serializable {
}