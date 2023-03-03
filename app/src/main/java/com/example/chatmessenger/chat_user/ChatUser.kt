package com.example.chatmessenger.chat_user

data class ChatUser(var login: String, var nickname: String, var phone: String, var email: String, var password: String, var isOnline: Boolean) :java.io.Serializable {

}