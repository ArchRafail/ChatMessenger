package com.example.chatmessenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chatmessenger.R
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.chat_user.ChatUserAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import kotlin.collections.ArrayList


class UsersFragment :Fragment() {

    private lateinit var usersTitleTextView : TextView
    private var users = ArrayList<ChatUser>()
    private var usersList: ListView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView :View = inflater.inflate(R.layout.fragment_users, container, false)
        usersTitleTextView = rootView.findViewById(R.id.usersTitleTextView)
        users = ArrayList()
        setInitialData()

        usersList = rootView.findViewById(R.id.users_list)
        val chatUserAdapter = ChatUserAdapter(this.context, R.layout.chat_users_list, users)
        usersList?.adapter = chatUserAdapter

        return rootView
    }

    private fun setInitialData() {
//        val usersLogin = resources.getStringArray(R.array.Users_list)
//        val usersPhone = resources.getStringArray(R.array.Phone_list)
//        val usersEmail = resources.getStringArray(R.array.Emails_list)
//        val usersPassword = resources.getStringArray(R.array.Passwords_list)
//        val usersPhoto = ArrayList<Int>()
//        for (index in usersLogin.indices) {
//            val userLogin = usersLogin[index].toString().lowercase()
//            val photoId = resources.getIdentifier(userLogin, "drawable", requireContext().packageName)
//            usersPhoto.add(photoId)
//        }
//        for (i in usersLogin.indices) {
//            users.add(ChatUser(usersLogin[i], usersPhone[i], usersEmail[i], usersPassword[i], usersPhoto[i]))
//        }
        val gson = Gson()
        val arrayListChatUserType = object : TypeToken<ArrayList<ChatUser>>() {}.type
        val file = File(context?.filesDir?.path, "users.json")
        if (file.exists()) {
            users = gson.fromJson(FileReader(file), arrayListChatUserType)
        }
    }

}