package com.arkurl.eventtimepiece.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkurl.eventtimepiece.data.local.model.Event
import com.arkurl.eventtimepiece.data.repository.EventRepository
import kotlinx.coroutines.launch
import java.time.Instant

class EventViewModel(private val repository: EventRepository): ViewModel() {
    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

    fun loadAllEvents() {
        viewModelScope.launch {
            val events = repository.queryAllEvents()
            _eventList.postValue(events)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.delete(event)
            loadAllEvents()
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repository.update(event)
            loadAllEvents()
        }
    }

    fun insertEvent(event: Event) {
        viewModelScope.launch {
            repository.insert(event)
            loadAllEvents()
        }
    }

    fun insert(name: String, description: String, parentEventId: Long?) {
        viewModelScope.launch {
            repository.insert(Event (
                id = 0,
                name = name,
                timeCost = 0,
                description = description,
                createTime = Instant.now(),
                updateTime = Instant.now(),
                parentEventId = parentEventId
            ))
            loadAllEvents()
        }
    }
}