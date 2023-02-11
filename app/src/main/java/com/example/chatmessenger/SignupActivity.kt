package com.example.chatmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.chatmessenger.action.Signup
import com.example.chatmessenger.chat_user.ChatUser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

class SignupActivity : AppCompatActivity() {

    private lateinit var users: ArrayList<ChatUser>
    private val logTag = "SignupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val loginButton: Button = findViewById(R.id.signin_relocate)
        loginButton.setOnClickListener {
            val intentLogin = Intent(this, SigninActivity::class.java)
            startActivity(intentLogin)
        }

        val signUpButton: Button = findViewById(R.id.signup_button)
        signUpButton.setOnClickListener(signUpClickListener)
    }

    private val signUpClickListener: View.OnClickListener = View.OnClickListener {
        var login = findViewById<TextView>(R.id.signup_login).text.toString()
        val email = findViewById<TextView>(R.id.signup_email).text.toString()
        val password = findViewById<TextView>(R.id.signup_password).text.toString()
        val passwordRepeat = findViewById<TextView>(R.id.signup_repeat_password).text.toString()

        users = ArrayList()
        val gson = Gson()
        val arrayListChatUserType = object : TypeToken<ArrayList<ChatUser>>() {}.type
        val fileUsers = File(this.filesDir.path, "users.json")

        if (fileUsers.exists()) {
            users = gson.fromJson(FileReader(fileUsers), arrayListChatUserType)
        } else {
            fileUsers.createNewFile()
        }

        val signup = Signup(login, email, password, passwordRepeat, users)
        if (signup.getUserCreated()) {
            val user = ChatUser(login, "none", email, password,
                resources.getIdentifier("blank_profile_picture_120.png", "drawable", this.packageName))
            users.add(user)

            val gsonUsersBuilder = GsonBuilder().setPrettyPrinting().create()
            val jsonUsers: String = gsonUsersBuilder.toJson(users)
            fileUsers.writeText(jsonUsers)

            val gsonUserBuilder = GsonBuilder().setPrettyPrinting().create()
            val jsonUser: String = gsonUserBuilder.toJson(user)
            val fileUser = File(this.filesDir.path, "currentUser.json")
            if (!fileUser.exists()) {
                fileUser.createNewFile()
            }
            fileUser.writeText(jsonUser)

            Toast.makeText(this, "User $login created", Toast.LENGTH_SHORT).show()
            Log.w(logTag, "User $login created")

            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
        } else {
            if (login.isBlank()) { login = "NoName"}
            Toast.makeText(this, "User $login was not created. " + signup.getMessage(),
                Toast.LENGTH_SHORT).show()
            Log.w(logTag, "User $login was not created. " + signup.getMessage())
        }
    }

}