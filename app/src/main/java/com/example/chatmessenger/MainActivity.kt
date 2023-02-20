package com.example.chatmessenger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.chatmessenger.chat_message.ChatMessage
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.database.DBHelper
import com.example.chatmessenger.fragment.ChatFragment
import com.example.chatmessenger.fragment.InfoFragment
import com.example.chatmessenger.fragment.PreferencesFragment
import com.example.chatmessenger.fragment.UsersFragment
import com.example.chatmessenger.service.MessageService
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity(), View.OnClickListener, ChatFragment.OnSelectedButtonListener{

    private val tag = "MainActivity"
    private val mIsDynamic = true
    private lateinit var usersButton: MaterialButton
    private lateinit var chatButton: MaterialButton
    private lateinit var preferencesButton: MaterialButton
    private lateinit var infoButton: MaterialButton

    private var currentFragment: Fragment? = null
    private var usersFragment: UsersFragment? = null
    private var chatFragment: ChatFragment? = null
    private var preferencesFragment: PreferencesFragment? = null
    private var infoFragment: InfoFragment? = null

    private lateinit var dbHelper: DBHelper

    private var br: BroadcastReceiver? = null

    companion object {
        const val BROADCAST_ACTION: String = "com.example.chatmessenger.servicebackbroadcast"
        const val STATUS_START = 100
        const val STATUS_FINISH = 200
        var PARAM_STATUS = "status"
        var PARAM_MESSAGE = "message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper.getInstance(this)

        startService(Intent(this, MessageService::class.java))

        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra(PARAM_STATUS, 0)

                if (status == STATUS_START) {
                    Log.d(tag, "onReceive: status = $status")
                }

                if (status == STATUS_FINISH) {
                    val message = intent.serializable<ChatMessage>(PARAM_MESSAGE)
                    Log.d(tag, "onReceive: status = $status message= ${message!!.message}")
                    dbHelper.addMessage(message.login, message.message, message.time)
                    var user: ChatUser? = null
                    val users : ArrayList<ChatUser> = dbHelper.allUsers
                    for (i in users.indices) {
                        if (users[i].getLogin() == message.login) {
                            user = users[i]
                            break
                        }
                    }
                    onButtonSelected(user!!.getNickname(), message.message)
                    if (currentFragment != null && currentFragment == chatFragment) {
                        val fragmentManager: FragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction().detach(currentFragment!!).commit()
                        fragmentManager.beginTransaction().attach(currentFragment!!).commit()
                    }
                }
            }
        }
        val intFilt = IntentFilter(BROADCAST_ACTION)
        registerReceiver(br, intFilt)

        usersButton = findViewById(R.id.users_button)
        usersButton.setOnClickListener(this)

        chatButton = findViewById(R.id.chat_button)
        chatButton.setOnClickListener(this)

        preferencesButton = findViewById(R.id.preferences_button)
        preferencesButton.setOnClickListener(this)

        infoButton = findViewById(R.id.information_button)
        infoButton.setOnClickListener(this)

        val fragmentManager = supportFragmentManager

        if (mIsDynamic) {
            usersFragment = UsersFragment()
            chatFragment = ChatFragment()
            preferencesFragment = PreferencesFragment()
            infoFragment = InfoFragment()

            val ft = fragmentManager.beginTransaction()
            ft.add(R.id.container, chatFragment!!, "")
            ft.commit()
            currentFragment = chatFragment
        }

    }

    override fun onClick(view: View?) {
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragment = when (view) {
            usersButton ->  usersFragment
            chatButton -> chatFragment
            preferencesButton -> preferencesFragment
            infoButton -> infoFragment
            else -> null
        }

        if (fragment == null) {
            showNotification("Unknown", "ERROR!!!")
            return
        }

        if (fragment == currentFragment) return

        currentFragment = fragment

        val ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.replace(R.id.container, currentFragment!!, "")
        ft.addToBackStack(null)
        ft.setCustomAnimations(
            R.anim.slide_in,   //enter
            R.anim.fade_out,   //exit
            R.anim.fade_in,   //pop enter
            R.anim.slide_out   //pop exit
        )
        ft.commit()
    }

    private fun showNotification(title: String, message: String) {
        Log.d(tag, "showNotification")
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "channel_id_01"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "My_Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            nm.createNotificationChannel(notificationChannel)
        }
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.drawable.logo_chat)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(message)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(contentIntent)
        val notification: Notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        nm.notify(0, notification)
    }

    override fun onButtonSelected(userName: String, messageStr: String) {
        showNotification(userName, messageStr)
    }

    override fun onDestroy() {
        val dbHelper: DBHelper = DBHelper.getInstance(this)
        val myPreferences : SharedPreferences = androidx.preference.PreferenceManager
            .getDefaultSharedPreferences(this)
        dbHelper.changeOnlineStatus(myPreferences.getString("Login", "NoName"), 0)
        stopService(Intent(this, MessageService::class.java))
        this.unregisterReceiver(br!!)
        super.onDestroy()
    }


    // serializable for bundle
    inline fun <reified T : java.io.Serializable> Bundle.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

    // serializable for intent
    inline fun <reified T : java.io.Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
}
