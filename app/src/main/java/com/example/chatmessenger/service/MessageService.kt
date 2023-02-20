package com.example.chatmessenger.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.chat_message.ChatMessage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MessageService: Service() {
    private lateinit var es: ExecutorService
    companion object {
        const val tag: String = "MessageService"
    }

    override fun onCreate() {
        super.onCreate()
        es = Executors.newFixedThreadPool(1)
        Log.d(tag, "MessageService starts to work.")
    }

    override fun onDestroy() {
        Log.d(tag, "MessageService stops to work.")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(tag, "MessageService onStartCommand")
        var i = 1
        var message: ChatMessage? = null
        var t: Long? = null
        while (i < 10) {
            when (i) {
                1 -> {
                    message = ChatMessage("Daddy", "Hi Roman. Are you at work now?", "15.02.23 18:14")
                    t = 5
                }
                3 -> {
                    message = ChatMessage("Daddy", "When you are coming at home?", "15.02.23 18:18")
                    t = 7
                }
                5 -> {
                    message = ChatMessage("Daddy", "We were at cinema today. With mam.", "15.02.23 18:20")
                    t = 4
                }
                7 -> {
                    message = ChatMessage("Daddy", "I apologise that Daniel was talk to you? ", "15.02.23 18:24")
                    t = 7
                }
                9 -> {
                    message = ChatMessage("Daddy", "He had to bring the material to you tomorrow. It is about repairing your apartment", "15.02.23 18:25")
                    t = 4
                    }
            }
            es.execute(MyRun(this, startId, message!!, t!!))
            i += 2
        }
        return super.onStartCommand(intent, flags, startId)
    }

    internal class MyRun(context: Context, startId: Int, message: ChatMessage, time: Long) : Runnable {

        private var context: Context
        private var startId: Int
        private var message: ChatMessage
        private var time: Long

        init {
            this.context = context
            this.startId = startId
            this.message = message
            this.time = time
        }

        override fun run() {
            val intent = Intent(MainActivity.BROADCAST_ACTION)
            try {
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START)
                context.sendBroadcast(intent)

                TimeUnit.SECONDS.sleep(time)

                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH)
                intent.putExtra(MainActivity.PARAM_MESSAGE, message)
                context.sendBroadcast(intent)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}