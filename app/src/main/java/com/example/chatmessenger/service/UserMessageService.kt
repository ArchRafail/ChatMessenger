package com.example.chatmessenger.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.chat_message.ChatMessage
import com.example.chatmessenger.chat_user.ChatUser
import com.example.chatmessenger.database.DBHelper
import com.example.chatmessenger.request.GetMessages
import com.example.chatmessenger.request.GetUsers
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class UserMessageService: Service() {
    private lateinit var es: ExecutorService
    companion object {
        const val tag: String = "UserMessageService"
    }

    override fun onCreate() {
        super.onCreate()
        es = Executors.newFixedThreadPool(2)
        Log.d(tag, "UserMessageService starts to work.")
    }

    override fun onDestroy() {
        Log.d(tag, "UserMessageService stops to work.")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(tag, "UserMessageService onStartCommand")
        var i = 1
        var users: ArrayList<ChatUser>
        var messages: ArrayList<ChatMessage>
        var t: Long
        while (i != null) {
            when (i) {
                3 -> {
                    val request = GetUsers()
                    request.execute()
                    users = request.retrieveUsers()
                    t = 5
                    es.execute(DownloadUsers(this, startId, users, t))
                }
                5 -> {
                    val request = GetMessages()
                    request.execute()
                    messages = request.retrieveMessages()
                    t = 8
                    es.execute(DownloadMessages(this, startId, messages, t))
                }
            }
            i += 1
            if (i > 5) {
                i = 1
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    internal class DownloadUsers(context: Context, startId: Int, users: ArrayList<ChatUser>, time: Long) : Runnable {

        private var context: Context
        private var startId: Int
        private var users: ArrayList<ChatUser>
        private var time: Long

        init {
            this.context = context
            this.startId = startId
            this.users = users
            this.time = time
        }

        override fun run() {
            val intent = Intent(MainActivity.BROADCAST_ACTION)
            try {
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START)
                intent.putExtra(MainActivity.PARAM_TASK, MainActivity.TASK_USER)
                context.sendBroadcast(intent)

                TimeUnit.SECONDS.sleep(time)

                val dbHelper: DBHelper = DBHelper.getInstance(context)
                dbHelper.updateAllUsers(users)
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH)
                intent.putExtra(MainActivity.PARAM_TASK, MainActivity.TASK_USER)
                intent.putExtra(MainActivity.PARAM_USERS, users)
                context.sendBroadcast(intent)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    internal class DownloadMessages(context: Context, startId: Int, messages: ArrayList<ChatMessage>, time: Long) : Runnable {

        private var context: Context
        private var startId: Int
        private var messages: ArrayList<ChatMessage>
        private var time: Long

        init {
            this.context = context
            this.startId = startId
            this.messages = messages
            this.time = time
        }

        override fun run() {
            val intent = Intent(MainActivity.BROADCAST_ACTION)
            try {
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START)
                intent.putExtra(MainActivity.PARAM_TASK, MainActivity.TASK_MESSAGE)
                context.sendBroadcast(intent)

                TimeUnit.SECONDS.sleep(time)

                val dbHelper: DBHelper = DBHelper.getInstance(context)
                val oldMessages = dbHelper.allMessages
                if (messages.size != oldMessages.size) {
                    var i = oldMessages.size
                    while (i < messages.size) {
                        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH)
                        intent.putExtra(MainActivity.PARAM_TASK, MainActivity.TASK_MESSAGE)
                        intent.putExtra(MainActivity.PARAM_MESSAGE, messages[i])
                        context.sendBroadcast(intent)
                        i++
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}