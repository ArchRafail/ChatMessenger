package com.example.chatmessenger.chat_message


class ChatMessage(login: String, message: String, time: String) {
    private var login: String
    private var message: String
    private var time: String

    fun getLogin(): String {
        return login
    }

    fun setLogin(login: String) {
        this.login = login
    }

    fun getMessage(): String {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getTime(): String {
        return time
    }

    fun setTime(time: String) {
        this.time = time
    }

    init {
        this.login = login
        this.message = message
        this.time = time

    }
}