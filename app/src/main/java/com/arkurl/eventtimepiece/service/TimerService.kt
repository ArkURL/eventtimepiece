package com.arkurl.eventtimepiece.service

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerService : Service() {
    private val binder = TimerBinder()
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> get() = _elapsedTime
    private var isRunning = false

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    companion object {
        private val TAG = TimerService::class.java.simpleName
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
        startTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PAUSE" -> pauseTimer()
            "ACTION_RESUME" -> startTimer()
            "ACTION_STOP" -> stopTimer()
        }
        return START_STICKY
    }

    fun startTimer() {
        if (isRunning) return
        isRunning = true
        runnable = object : Runnable {
            override fun run() {
                _elapsedTime.value = _elapsedTime.value + 1
                _elapsedTime.tryEmit(_elapsedTime.value) // 🔴 确保 `StateFlow` 立即通知更新
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable!!, 1000)
    }

    fun pauseTimer() {
        isRunning = false
        runnable?.let { handler.removeCallbacks(it) }
    }

    fun stopTimer() {
        isRunning = false
        runnable?.let { handler.removeCallbacks(it) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("timer_channel", "计时器服务", NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle("计时器正在运行")
            .setContentText("计时中...")
            .setSmallIcon(R.drawable.timer_24px)
            .build()
        startForeground(1, notification)
    }
}