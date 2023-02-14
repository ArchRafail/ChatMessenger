package com.example.chatmessenger.action

import android.content.Context
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.database.DBHelper


class Signin(loginCurrent: String, passwordCurrent: String, context: Context) {

    private var users: ArrayList<ChatUser> = ArrayList()
    private lateinit var user: ChatUser
    private var isExist :Boolean = false
    private var dBHelper: DBHelper? = null

    init{
        dBHelper = DBHelper.getInstance(context)
        if (dBHelper != null) {
            users = dBHelper!!.allUsers
        }

        for (i in this.users.indices) {
            if (loginCurrent == users[i].getLogin()) {
                if (dBHelper!!.md5(passwordCurrent) == users[i].getPassword()) {
                    isExist = true
                    user = users[i]
                    user.setIsOnline(true)
                    users[i].setIsOnline(true)
                }
            }
        }

    }

    fun getIsExist() :Boolean { return isExist}

}