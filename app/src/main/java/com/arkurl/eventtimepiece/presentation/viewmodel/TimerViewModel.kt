package com.arkurl.eventtimepiece.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arkurl.eventtimepiece.data.repository.TimerRepository

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TimerRepository(application)
    private val _isTimerRunning = MutableLiveData<Boolean>(true)

    val isTimerRunning: LiveData<Boolean> get() = _isTimerRunning
    val elapsedTime: LiveData<Long> = repository.elapsedTime

    companion object {
        private val TAG = TimerViewModel::class.java.simpleName
    }

    fun shiftTimer() {
        Log.d(TAG, "shiftTimer: ${_isTimerRunning.value}")
        if (isTimerRunning.value == true) {
            pauseTimer()
        } else {
            resumeTimer()
        }
    }


    fun startTimer() {
        repository.startService()
        _isTimerRunning.postValue(true)
    }
    fun pauseTimer() {
        repository.pauseService()
        _isTimerRunning.postValue(false)
    }
    fun resumeTimer() {
        Log.d(TAG, "resumeTimer: ")
        repository.resumeService()
        _isTimerRunning.postValue(true)
    }
    fun stopTimer() {
        repository.stopService()
        _isTimerRunning.postValue(false)
    }

}