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
import com.example.chatmessenger.database.DBHelper


class UsersFragment :Fragment() {

    private lateinit var usersTitleTextView : TextView
    private var users = ArrayList<ChatUser>()
    private var usersList: ListView? = null
    private var dBHelper: DBHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView :View = inflater.inflate(R.layout.fragment_users, container, false)
        usersTitleTextView = rootView.findViewById(R.id.usersTitleTextView)

        users = ArrayList()
        dBHelper = DBHelper.getInstance(context)
        if (dBHelper != null) {
            users = dBHelper!!.allUsers
        }

        usersList = rootView.findViewById(R.id.users_list)
        val chatUserAdapter = ChatUserAdapter(this.context, R.layout.chat_users_list, users)
        usersList?.adapter = chatUserAdapter

        return rootView
    }

}