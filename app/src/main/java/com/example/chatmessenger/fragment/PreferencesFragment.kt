package com.example.chatmessenger.fragment

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.chatmessenger.R
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.database.DBHelper
import com.google.android.material.button.MaterialButton
import org.apache.commons.io.IOUtils
import java.io.*
import kotlin.collections.ArrayList


class PreferencesFragment : Fragment() {

    private lateinit var nicknameButton : MaterialButton
    private lateinit var avatarImage : ImageButton
    private lateinit var avatarButton : MaterialButton
    private lateinit var phoneButton : MaterialButton
    private lateinit var emailButton : MaterialButton
    private lateinit var passwordButton : MaterialButton
    private lateinit var login: String
    private lateinit var users :ArrayList<ChatUser>
    private var imageUri :Uri? = null
    private var dBHelper :DBHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_preferences, container, false)

        nicknameButton = rootView.findViewById(R.id.nickname_button)
        avatarImage = rootView.findViewById(R.id.avatar_image)
        avatarButton = rootView.findViewById(R.id.avatar_button)
        phoneButton = rootView.findViewById(R.id.phone_button)
        emailButton = rootView.findViewById(R.id.email_button)
        passwordButton = rootView.findViewById(R.id.password_button)

        dBHelper = DBHelper.getInstance(context)
        if (dBHelper != null) {
            users = dBHelper!!.allUsers
        }

        val myPreferences : SharedPreferences = androidx.preference.PreferenceManager
            .getDefaultSharedPreferences(requireContext())
        this.login = myPreferences.getString("Login", "NoName")!!
        val fileName = login.lowercase()

        val defaultPhoto = "blank_profile_picture_120"

        val drawableIdentifier = context?.resources?.getIdentifier(fileName, "drawable", context?.packageName)
        val imgFile = File(context?.filesDir?.path, "$fileName.jpg")
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            avatarImage.setImageBitmap(myBitmap)
        } else if (drawableIdentifier != null && drawableIdentifier > 0) {
            avatarImage.setImageResource(drawableIdentifier)
        } else {
            avatarImage.setImageResource(this.resources
                .getIdentifier(defaultPhoto, "drawable", context?.packageName))
        }

        nicknameButton.setOnClickListener {
            val newNicknameField = rootView.findViewById<TextView>(R.id.new_nickname)
            val newNickname = newNicknameField.text.toString()
            if (!nicknameValidation(newNickname)) {
                showMessage("Wrong Nickname! Length of nickname has to be more than 2.")
                return@setOnClickListener
            }
            if (dBHelper!!.writeInfo(login, "nickname", newNickname)) {
                showMessage()
            } else {
                showMessage("An issue with saving updated info.")
            }
            newNicknameField.text = ""
        }

        phoneButton.setOnClickListener {
            val newPhoneField = rootView.findViewById<TextView>(R.id.new_phone)
            val newPhone = newPhoneField.text.toString()
            if (!phoneValidation(newPhone)) {
                showMessage("Wrong phone number!")
                return@setOnClickListener
            }

            if (dBHelper!!.writeInfo(login, "phone", newPhone)) {
                showMessage()
            } else {
                showMessage("An issue with saving updated info.")
            }
            newPhoneField.text = ""
        }

        emailButton.setOnClickListener {
            val newEmailField = rootView.findViewById<TextView>(R.id.new_email)
            val newEmail = newEmailField.text.toString()
            if (!emailValidation(newEmail)) {
                showMessage("Wrong email!")
                return@setOnClickListener
            }
            if (dBHelper!!.writeInfo(login, "email", newEmail)) {
                showMessage()
            } else {
                showMessage("An issue with saving updated info.")
            }
            newEmailField.text = ""
        }

        passwordButton.setOnClickListener {
            val oldPasswordField = rootView.findViewById<TextView>(R.id.old_password)
            val newPasswordField = rootView.findViewById<TextView>(R.id.new_password)
            val newPasswordRepeatField = rootView.findViewById<TextView>(R.id.new_password_repeat)
            val oldPassword = oldPasswordField.text.toString()
            val newPassword = newPasswordField.text.toString()
            val newPasswordRepeat = newPasswordRepeatField.text.toString()
            if (!passwordValidation(oldPassword, newPassword, newPasswordRepeat)) {
                showMessage("Old password is not correct or new passwords does not match.")
                return@setOnClickListener
            }
            if (dBHelper!!.writeInfo(login, "password", newPassword)) {
                showMessage()
            } else {
                showMessage("An issue with saving updated info.")
            }
            oldPasswordField.text = ""
            newPasswordField.text = ""
            newPasswordRepeatField.text = ""
        }

        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent()
        ) {
            imageUri = it
            val imageStream: InputStream? = imageUri?.let { it1 ->
                context?.contentResolver?.openInputStream(it1)
            }
            var selectedImage = BitmapFactory.decodeStream(imageStream)

            selectedImage = getResizedBitmap(
                selectedImage,
                (context?.resources?.getDimension(R.dimen.pref_fragment_avatar_dimension)?.toInt())
            )
            avatarImage.setImageBitmap(selectedImage)
        }

        avatarImage.setOnClickListener {
            galleryImage.launch("image/*")
        }

        avatarButton.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(context, "Nothing to change. Pick up a new image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val oldUri : Uri? = if (imgFile.exists()) {
                Uri.fromFile(imgFile)
            } else if (drawableIdentifier != null && drawableIdentifier > 0) {
                Uri.parse("android.resource://com.example.chatmessenger/drawable/$fileName")
            } else {
                Uri.parse("android.resource://com.example.chatmessenger/drawable/$defaultPhoto")
            }

            if (imageUri == oldUri) {
                Toast.makeText(context, "Nothing to change. Pick up a new image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageUri?.let { it1 ->
                context?.contentResolver?.getType(
                    it1
                )
            })
            val newFile = File(context?.filesDir?.absolutePath, "$fileName.${extension}")

            var inputStream: InputStream? = null
            val byteStream = ByteArrayOutputStream()
            var fileOutputStream: FileOutputStream? = null
            try {
                inputStream = imageUri?.let { it1 -> context?.contentResolver?.openInputStream(it1) }
                fileOutputStream = FileOutputStream(newFile)

                IOUtils.copy(inputStream, byteStream)
                val bytes = byteStream.toByteArray()

                fileOutputStream.write(bytes)
//                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, byteStream.size())
                showMessage()
            } catch (e: Exception) {
                showMessage("Failed to copy image.")
            } finally {
                inputStream?.close()
                fileOutputStream?.close()
                byteStream.close()
            }
        }

        return rootView
    }

    private fun nicknameValidation(nickname: String?) :Boolean {
        if (nickname.isNullOrBlank() || (nickname.length < 3)) {
            return false
        }
        return true
    }

    private fun phoneValidation(phone: String?): Boolean {
        if (phone.isNullOrBlank()) {
            return false
        }
        if (phone.length != 13) {
            return false
        }
        if (phone.substring(0, 4) != "+380") {
            return false
        }
        return true
    }

    private fun emailValidation(email: CharSequence?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun passwordValidation(oldPassword: String, newPassword: String,
                                   newPasswordRepeat: String) :Boolean {
        if (oldPassword.isNullOrBlank() || newPassword.isNullOrBlank() || newPasswordRepeat.isNullOrBlank()) {
            return false
        }
        for (i in users.indices) {
            if (users[i].getLogin()==login && users[i].getPassword()!=dBHelper!!.md5(oldPassword)) {
                return false
            }
        }
        if (newPassword != newPasswordRepeat) {
            return false
        }
        return true
    }

    private fun showMessage(message: String = "An information about user $login is saved.") {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int?): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize ?: 130
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize ?: 130
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

}