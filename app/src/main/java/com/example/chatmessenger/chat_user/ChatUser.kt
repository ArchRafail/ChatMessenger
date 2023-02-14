package com.example.chatmessenger.chat_user

class ChatUser(login: String, nickname: String, phone: String, email: String, password: String, isOnline: Boolean) {
    private var login: String
    private var nickname: String
    private var phone: String
    private var email: String
    private var password: String
    private var isOnline: Boolean

    fun getLogin(): String {
        return login
    }

    fun getNickname(): String {
        return nickname
    }

    fun getPhone(): String {
        return phone
    }

    fun getEmail(): String {
        return email
    }

    fun getPassword(): String {
        return password
    }

    fun getIsOnline(): Boolean {
        return isOnline
    }

    fun setIsOnline(isOnline: Boolean) {
        this.isOnline = isOnline
    }

    init {
        this.login = login
        this.nickname = nickname
        this.phone = phone
        this.email = email
        this.password = password
        this.isOnline = isOnline
    }
}