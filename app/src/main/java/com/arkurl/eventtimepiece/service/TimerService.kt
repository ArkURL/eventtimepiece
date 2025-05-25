package com.arkurl.eventtimepiece.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.local.dao.EventDao
import com.arkurl.eventtimepiece.data.local.database.AppDatabase
import com.arkurl.eventtimepiece.util.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerService : Service() {
    private val binder = TimerBinder()
    private var eventId: Long = -1
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> get() = _elapsedTime
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    private lateinit var database: AppDatabase
    private lateinit var eventDao: EventDao


    companion object {
        private val TAG = TimerService::class.java.simpleName
        private val CHANNEL_ID: String
            get() = "TimerServiceChannel"
        private val CHANNEL_NAME: String
            get() = "Timer Service Channel"
        private val NOTIFICATION_ID: Int
            get() = 1
    }

    inner class TimerBinder: Binder() {
        fun getService(): TimerService = this@TimerService

        fun getElapsedTime(): Long = _elapsedTime.value
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: ")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: service created")
        database = AppDatabase.getDatabaseInstance(this)
        eventDao = database.eventDao()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: service destroyed")
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: service started")
        eventId = intent?.getLongExtra("EVENT_ID", -1L) ?: -1L
        if (eventId == -1L) {
            Log.d(TAG, "onStartCommand: invalid event")
            stopSelf()
            return START_NOT_STICKY
        }

        serviceScope.launch {
            val event = eventDao.querySpecifyEvent(eventId)
            _elapsedTime.value = event.timeCost
        }

        startForegroundService()
        return START_STICKY
    }

    fun startTimer(event) {
        if (timerJob != null) return // 防止重复启动计时器

        timerJob = serviceScope.launch {
            while (isActive) {
                _elapsedTime.value += 1 // 直接自增，避免 `elapsedTime.value` 的多线程访问问题
                updateNotification()
                delay(1000) // 1秒更新一次
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.timer_is_running))
            .setContentText(TimeUtils.formatTimeCost(_elapsedTime.value))
            .setSmallIcon(R.drawable.timer_24px)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
}