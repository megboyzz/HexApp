package ru.megboyzz.hexapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import kotlinx.coroutines.*

val Service.LOG_TAG: String
    get() = this::class.java.simpleName

class HexService : Service() {

    private lateinit var statusReceiver: StatusReceiver
    private lateinit var filter: IntentFilter

    init {
        Log.i(LOG_TAG, "constructor!")
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(LOG_TAG, "onBind!")

        return null
    }

    override fun onCreate() {
        super.onCreate()
        statusReceiver = StatusReceiver(HexGenerator(this))
        filter = IntentFilter()
        filter.addAction("ru.megboyzz.hexapp.action.TOGGLE_READING_MODE")
        filter.addAction("ru.megboyzz.hexapp.action.LEAVE_READING_MODE")
        filter.addAction("ru.megboyzz.hexapp.action.CHECK_STATUS")
        Log.i(LOG_TAG, "service is created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        statusReceiver.serviceStatus = ServiceStatus.STOPPED
        unregisterReceiver(statusReceiver)
        Log.i(LOG_TAG, "service is destroyed!")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(LOG_TAG, "Command received!")

        startForeground()

        registerReceiver(statusReceiver, filter)

        statusReceiver.serviceStatus = ServiceStatus.RUNNING

        return START_STICKY

    }


    private fun startForeground() {
        val channelId =
            createNotificationChannel("my_service", "My Background Service")

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


}