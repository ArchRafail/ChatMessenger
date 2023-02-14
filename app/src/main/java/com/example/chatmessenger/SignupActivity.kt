package com.example.chatmessenger

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.chatmessenger.action.Signup
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.database.DBHelper


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

        val dBHelper = DBHelper.getInstance(this)
        users = dBHelper!!.allUsers

        val signup = Signup(login, email, password, passwordRepeat, users)
        if (signup.getUserCreated()) {
            dBHelper.addUser(login, email, password)
            users = dBHelper.allUsers

            val myPreferences : SharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(this)
            val myEditor : SharedPreferences.Editor = myPreferences.edit()
            myEditor.putString("Login", login)
            myEditor.commit()

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