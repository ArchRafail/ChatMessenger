package com.example.chatmessenger.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chatmessenger.R
import com.example.chatmessenger.chat_user.ChatUser
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.IOUtils
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class PreferencesFragment : Fragment() {

    private lateinit var nicknameButton : MaterialButton
    private lateinit var avatarImage : ImageButton
    private lateinit var avatarButton : MaterialButton
    private lateinit var phoneButton : MaterialButton
    private lateinit var emailButton : MaterialButton
    private lateinit var passwordButton : MaterialButton
    private lateinit var fileUser :File
    private lateinit var fileUsers :File
    private lateinit var user : ChatUser
    private lateinit var users : ArrayList<ChatUser>
    private var imageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_preferences, container, false)

        fileUser = File(context?.filesDir?.path , "currentUser.json")
        fileUsers = File(context?.filesDir?.path , "users.json")

        nicknameButton = rootView.findViewById(R.id.nickname_button)
        avatarImage = rootView.findViewById(R.id.avatar_image)
        avatarButton = rootView.findViewById(R.id.avatar_button)
        phoneButton = rootView.findViewById(R.id.phone_button)
        emailButton = rootView.findViewById(R.id.email_button)
        passwordButton = rootView.findViewById(R.id.password_button)

        val gson = Gson()
        user = gson.fromJson(FileReader(fileUser), ChatUser::class.java)
        val arrayListChatUsersType = object : TypeToken<ArrayList<ChatUser>>() {}.type
        users = gson.fromJson(FileReader(fileUsers), arrayListChatUsersType)

        val defaultPhoto = "blank_profile_picture_120"
        val fileName = user.getLogin().lowercase()
        val imgFile = File(context?.filesDir?.path, "$fileName.jpg")
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            avatarImage.setImageBitmap(myBitmap)
        } else if (user.getPhotoResource() > 0) {
            avatarImage.setImageResource(user.getPhotoResource())
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
            user.setNickname(newNickname)
            for (index in users.indices) {
                if (users[index].getLogin() == user.getLogin()) {
                    users[index].setNickname(newNickname)
                    break
                }
            }
            if (writeInfo()) {
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
            user.setPhone(newPhone)
            for (index in users.indices) {
                if (users[index].getLogin() == user.getLogin()) {
                    users[index].setPhone(newPhone)
                    break
                }
            }
            if (writeInfo()) {
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
            user.setEmail(newEmail)
            for (index in users.indices) {
                if (users[index].getLogin() == user.getLogin()) {
                    users[index].setEmail(newEmail)
                    break
                }
            }
            if (writeInfo()) {
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
            user.setPassword(newPassword)
            for (index in users.indices) {
                if (users[index].getLogin() == user.getLogin()) {
                    users[index].setPassword(newPassword)
                    break
                }
            }
            if (writeInfo()) {
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
            val drawable = avatarImage.drawable
            var photoResource  = user.getPhotoResource()
            if (photoResource == 0) {
                photoResource = resources.getIdentifier(
                "blank_profile_picture_120",
                "drawable",
                context?.packageName)
            }
            if (drawable.bytesEqualTo(ContextCompat.getDrawable(requireContext(), photoResource)) &&
                    drawable.pixelsEqualTo(ContextCompat.getDrawable(requireContext(), photoResource))) {
                Toast.makeText(context, "Nothing to change. Pick up a new image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newFileName = user.getLogin().lowercase()
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageUri?.let { it1 ->
                context?.contentResolver?.getType(
                    it1
                )
            })
            val newFile = File(context?.filesDir?.absolutePath, "$newFileName.${extension}")

            var inputStream: InputStream? = null
            val byteStream = ByteArrayOutputStream()
            var fileOutputStream: FileOutputStream? = null
            try {
                inputStream = imageUri?.let { it1 -> context?.contentResolver?.openInputStream(it1) }
                fileOutputStream = FileOutputStream(newFile)

                IOUtils.copy(inputStream, byteStream)
                val bytes = byteStream.toByteArray()

                fileOutputStream.write(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, byteStream.size())                 //      Get a copied image
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to copy image", Toast.LENGTH_SHORT).show()
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

    private fun passwordValidation(oldPassword: String, newPassword: String, newPasswordRepeat: String) :Boolean {
        if (oldPassword.isNullOrBlank() || newPassword.isNullOrBlank() || newPasswordRepeat.isNullOrBlank()) {
            return false
        }
        if (oldPassword != user.getPassword()) {
            return false
        }
        if (newPassword != newPasswordRepeat) {
            return false
        }
        return true
    }

    private fun writeInfo() :Boolean {
        return try {
            val gsonUsersBuilder = GsonBuilder().setPrettyPrinting().create()
            val jsonUsers: String = gsonUsersBuilder.toJson(users)
            fileUsers.writeText(jsonUsers)

            val gsonUserBuilder = GsonBuilder().setPrettyPrinting().create()
            val jsonUser: String = gsonUserBuilder.toJson(user)
            fileUser.writeText(jsonUser)
            true
        } catch (ex :java.lang.Exception) {
            false
        }
    }

    private fun showMessage(message: String = "An information about user ${user.getLogin()} is saved.") {
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

    fun <T : Drawable> T.bytesEqualTo(t: T?) = toBitmap().bytesEqualTo(t?.toBitmap(), true)

    fun <T : Drawable> T.pixelsEqualTo(t: T?) = toBitmap().pixelsEqualTo(t?.toBitmap(), true)

    private fun Bitmap.bytesEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false) = otherBitmap?.let { other ->
        if (width == other.width && height == other.height) {
            val res = toBytes().contentEquals(other.toBytes())
            if (shouldRecycle) {
                doRecycle().also { otherBitmap.doRecycle() }
            }
            res
        } else false
    } ?: kotlin.run { false }

    private fun Bitmap.pixelsEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false) = otherBitmap?.let { other ->
        if (width == other.width && height == other.height) {
            val res = Arrays.equals(toPixels(), other.toPixels())
            if (shouldRecycle) {
                doRecycle().also { otherBitmap.doRecycle() }
            }
            res
        } else false
    } ?: kotlin.run { false }

    private fun Bitmap.doRecycle() {
        if (!isRecycled) recycle()
    }

    private fun <T : Drawable> T.toBitmap(): Bitmap {
        if (this is BitmapDrawable) return bitmap

        val drawable: Drawable = this
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun Bitmap.toBytes(): ByteArray = ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.toByteArray()
    }

    private fun Bitmap.toPixels() = IntArray(width * height).apply { getPixels(this, 0, width, 0, 0, width, height) }

}