package com.arkurl.eventtimepiece.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arkurl.eventtimepiece.service.TimerService
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimerRepository(private val context: Application) {
    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> get() = _elapsedTime

    private var timerService: TimerService? = null
    private var serviceConnection: ServiceConnection? = null

    companion object {
        private val TAG = TimerRepository::class.java.simpleName
    }

    init {
        bindService()
    }

    private fun bindService() {
        val intent = Intent(context, TimerService::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.TimerBinder
                timerService = binder.getService()

                // 监听 Service 的 StateFlow 并更新 LiveData
                timerService?.let { service ->
                    CoroutineScope(Dispatchers.Main).launch {
                        service.elapsedTime.collect { time ->
                            _elapsedTime.postValue(time)
                        }
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
            }
        }
        context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    fun startService() {
        val intent = Intent(context, TimerService::class.java)
        context.startForegroundService(intent)
        bindService()  // 确保每次启动 Service 都绑定
    }

    fun pauseService() {
        CoroutineScope(Dispatchers.IO).launch {
            timerService?.pauseTimer()
        }
    }

    fun resumeService() {
        CoroutineScope(Dispatchers.IO).launch {
            timerService?.startTimer()
        }
    }

    fun stopService() {
        timerService?.stopTimer()

        // 🔴 只有在 serviceConnection 不为空时才解绑
        serviceConnection?.let {
            try {
                context.unbindService(it)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Service not registered: ${e.message}")
            }
            serviceConnection = null
        }
    }
}