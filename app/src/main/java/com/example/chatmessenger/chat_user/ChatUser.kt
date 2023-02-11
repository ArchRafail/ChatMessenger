package com.example.chatmessenger.chat_user

class ChatUser(login: String, phone: String, email: String, password: String, photoResource: Int) {
    private var login: String
    private var nickname: String
    private var phone: String
    private var email: String
    private var password: String
    private var photoResource: Int

    fun getLogin(): String {
        return login
    }

    fun setLogin(login: String) {
        this.login = login
    }

    fun getNickname(): String {
        return nickname
    }

    fun setNickname(nickname: String) {
        this.nickname = nickname
    }

    fun getPhone(): String {
        return phone
    }

    fun setPhone(phone: String) {
        this.phone = phone
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getPhotoResource(): Int {
        return photoResource
    }

    fun setPhotoResource(photoResource: Int) {
        this.photoResource = photoResource
    }

    override fun toString(): String {
        return "[login: ${this.login}, nickname: ${this.nickname}, phone: ${this.phone}, email: ${this.email}, password: ${this.password}, photoResource: ${this.photoResource}]"
    }

    init {
        this.login = login
        this.nickname = login
        this.phone = phone
        this.email = email
        this.password = password
        this.photoResource = photoResource
    }
}