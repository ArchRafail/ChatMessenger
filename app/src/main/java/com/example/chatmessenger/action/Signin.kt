package com.example.chatmessenger.action

import android.content.Context
import com.example.chatmessenger.chat_user.ChatUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader


class Signin(userCurrent: String, passwordCurrent: String, context: Context) {

    private var users: ArrayList<ChatUser>
    private lateinit var user: ChatUser
    private var isExist :Boolean = false

    init{

        val gson = Gson()
        val arrayListChatUserType = object : TypeToken<ArrayList<ChatUser>>() {}.type
        val file = File(context.filesDir.path, "users.json")
        users = ArrayList()
        if (file.exists()) {
            users = gson.fromJson(FileReader(file), arrayListChatUserType)

            for (i in this.users.indices) {
                if (userCurrent == users[i].getLogin()) {
                    if (passwordCurrent == users[i].getPassword()) {
                        isExist = true
                        user = users[i]
                    }
                }
            }
        }
    }

    fun getIsExist() :Boolean { return isExist}

    fun getUser() :ChatUser {return user}

}