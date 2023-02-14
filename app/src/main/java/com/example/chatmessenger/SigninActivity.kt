package com.example.chatmessenger

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatmessenger.action.Signin
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.database.DBHelper


class SigninActivity : AppCompatActivity() {

    private val logTag = "SigninActivity"
    private var userEmail = ""
    private var dBHelper: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        dBHelper = DBHelper.getInstance(this)

        if (dBHelper != null) {
            dBHelper!!.setInitialData()
        }

        val signupButton: Button = findViewById(R.id.signup_relocate)
        signupButton.setOnClickListener{
            val intentSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentSignup)
        }

        val signInButton: Button = findViewById(R.id.signin_button)
        signInButton.setOnClickListener(signInClickListener)

        val forgotButton: TextView = findViewById(R.id.forgot_button)
        forgotButton.setOnClickListener{
            showDialog()
        }
    }

    private val signInClickListener: View.OnClickListener = View.OnClickListener {
        val login = findViewById<TextView>(R.id.signin_login).text.toString()
        val password = findViewById<TextView>(R.id.signin_password).text.toString()
        val signIn = Signin(login, password, this)
        if (signIn.getIsExist()) {
            Log.w(logTag, "User $login come to the messenger.")

            val myPreferences :SharedPreferences  = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(this)
            val myEditor :SharedPreferences.Editor = myPreferences.edit()
            myEditor.putString("Login", login)
            myEditor.commit()

            dBHelper?.changeOnlineStatus(myPreferences.getString("Login", "NoName"), 1)

            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
        } else {
            Toast.makeText(this, "The user $login with the entered password is not registered.", Toast.LENGTH_SHORT).show()
            Log.w(logTag, "User $login makes a mistake in login or password.")
        }
    }

    private fun showDialog(){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
        val title: TextView = view.findViewById(R.id.dialog_title)
        val detail: TextView = view.findViewById(R.id.dialog_text)
        val input = view.findViewById<TextView>(R.id.dialog_request_email)

        title.text = resources.getString(R.string.forgot_password_title)
        detail.text = resources.getString(R.string.forgot_password_text)

        val okButton: Button = view.findViewById(R.id.dialog_ok_button)
        okButton.setOnClickListener {
            userEmail = input.text.toString()
            val password = lookingPassword()
            if (password.isNotBlank()) {
                Toast.makeText(
                    view.context,
                    "Dear $userEmail, Your password is $password",
                    Toast.LENGTH_SHORT
                ).show()
                sendingPassword(password)
            }
            userEmail = ""
            dialog?.dismiss()
        }

        val cancelButton: Button = view.findViewById(R.id.dialog_cancel_button)
        cancelButton.setOnClickListener {
            dialog?.cancel()
        }

        builder.setView(view)
        dialog = builder.create()
        dialog.show()
    }

    private fun lookingPassword() :String {
        if (userEmail.isNotBlank()) {
            val dBHelper: DBHelper = DBHelper.getInstance(this)
            val users: ArrayList<ChatUser> = dBHelper.allUsers
            for (i in users.indices) {
                if (userEmail == users[i].getEmail()) {
                    return dBHelper.getPasswordByEmail(userEmail)
                }
            }
        }
        return ""
    }

    private fun sendingPassword(password: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
        i.putExtra(Intent.EXTRA_SUBJECT, "Restoring password - Chat Messenger")
        i.putExtra(Intent.EXTRA_TEXT, "Dear $userEmail, Your password is $password. Please, " +
                "as soon as possible change it!")
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "There are no email clients installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}