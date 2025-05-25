package com.arkurl.eventtimepiece.presentation.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arkurl.eventtimepiece.data.repository.TimerRepository
import com.arkurl.eventtimepiece.service.TimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TimerRepository(application)
    val elapsedTime: StateFlow<Long> get() = repository.elapsedTime
    private var isRunning: Boolean = false


    fun bindService() {
        repository.bindService()
    }

    fun unbindService() {
        repository.unbindService()
    }

    fun startTimer(eventId: Long) {
        if (isRunning) return
        isRunning = true
        repository.startTimer(eventId)
    }

    fun pauseTimer(eventId: Long) {
        if (!isRunning) return
        isRunning = false
        repository.pauseTimer(eventId)
    }

    fun toggleTimer(eventId: Long) {
        if (isRunning) {
            pauseTimer(eventId)
        } else {
            startTimer(eventId)
        }
    }
}