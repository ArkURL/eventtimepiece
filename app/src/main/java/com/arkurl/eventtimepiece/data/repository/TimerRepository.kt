package com.arkurl.eventtimepiece.data.repository

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.arkurl.eventtimepiece.service.TimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerRepository(private val context: Context) {
    private var _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> get() = _elapsedTime

    private var serviceBinder: TimerService.TimerBinder? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBinder = service as TimerService.TimerBinder

            CoroutineScope(Dispatchers.Main).launch {
                serviceBinder?.getService()?.elapsedTime?.collect { time ->
                    _elapsedTime.value = time
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBinder = null
        }
    }

    fun bindService() {
        val intent = Intent(context, TimerService::class.java).apply {
            putExtra("EVENT_ID", )
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        context.unbindService(serviceConnection)
    }

    fun startTimer() {
        serviceBinder?.getService()?.startTimer()
    }

    fun pauseTimer() {
        serviceBinder?.getService()?.pauseTimer()
    }
}