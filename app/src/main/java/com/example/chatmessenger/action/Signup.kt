package com.example.chatmessenger.action

import android.text.TextUtils
import android.util.Patterns
import com.example.chatmessenger.chat_user.ChatUser


class Signup(login: String, email: String, password: String, passwordRepeat: String, users: ArrayList<ChatUser>) {

    private var isExist = false
    private var userCreated = true
    private var message = ""

    init {
        if (checkFields(login, email, password, passwordRepeat)) {
            userCreated = false
            message = "Fields must not be empty!"
        }
        if(userCreated && !isValidEmail(email)) {
            userCreated = false
            message = "Email is not correct!"
        }
        if(userCreated && !validatePasswords(password, passwordRepeat)) {
            userCreated = false
            message = "Passwords must match!"
        }

        if (users.isNotEmpty()) {
            for (i in users.indices) {
                if (login == users[i].getLogin()) {
                    isExist = true
                    message = "A user with this login is already registered!"
                }
            }
        }
        if (userCreated && isExist) {
            userCreated = false
        }
    }

    private fun checkFields(login: String, email: String, password: String, passwordRepeat: String): Boolean {
        return (login.isBlank() || email.isBlank() || password.isBlank() || passwordRepeat.isBlank())
    }

    private fun isValidEmail(email: CharSequence?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePasswords(password: String, passwordRepeat: String) :Boolean {
        return passwordRepeat == password
    }

    fun getUserCreated() :Boolean { return userCreated }

    fun getMessage() :String { return message }

}