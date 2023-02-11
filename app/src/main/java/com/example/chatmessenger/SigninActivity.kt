package com.example.chatmessenger

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatmessenger.action.Signin
import com.google.gson.GsonBuilder
import java.io.*


class SigninActivity : AppCompatActivity() {

    private val logTag = "SigninActivity"
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val usersFile = File(this.filesDir.path, "users.json")
        val currentUserFile = File(this.filesDir.path, "currentUser.json")
        val messagesFile = File(this.filesDir.path, "messages.json")
        if (!usersFile.exists() && !currentUserFile.exists() && !messagesFile.exists()) {
            copyAssets()
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
        val user = findViewById<TextView>(R.id.signin_login).text.toString()
        val password = findViewById<TextView>(R.id.signin_password).text.toString()
        val signIn = Signin(user, password, this)
        if (signIn.getIsExist()) {
            Log.w(logTag, "User $user come to the messenger.")

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonUser: String = gson.toJson(signIn.getUser())
            val file = File(this.filesDir.path, "currentUser.json")

            if (!file.exists()) {
                file.createNewFile()
            }
            file.writeText(jsonUser)

            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
        } else {
            Toast.makeText(this, "The user $user with the entered password is not registered.", Toast.LENGTH_SHORT).show()
            Log.w(logTag, "User $user makes a mistake in login or password.")
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
            val emails: Array<String> = resources.getStringArray(R.array.Emails_list)
            val passwords: Array<String> = resources.getStringArray(R.array.Passwords_list)
            for (i in emails.indices) {
                if (userEmail == emails[i]) { return passwords[i] }
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

    private fun copyAssets() {
        val assetManager = assets
        var files: Array<String>? = null
        try {
            files = assetManager.list("")
        } catch (e: IOException) {
            Log.e(logTag, "Failed to get asset file list.", e)
        }
        for (filename in files!!) {
            if (filename == "currentUser.json" || filename == "messages.json" || filename == "users.json") {
                var input: InputStream?
                var out: OutputStream?
                try {
                    input = assetManager.open(filename)
                    val outDir = this.filesDir.path
                    val outFile = File(outDir, filename)
                    out = FileOutputStream(outFile)
                    copyFile(input, out)
                    input.close()
                    out.flush()
                    out.close()
                } catch (e: IOException) {
                    Log.e(logTag, "Failed to copy asset file: $filename", e)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(input: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

}