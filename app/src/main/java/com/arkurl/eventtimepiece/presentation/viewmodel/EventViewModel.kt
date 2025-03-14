package com.arkurl.eventtimepiece.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkurl.eventtimepiece.data.local.model.Event
import com.arkurl.eventtimepiece.data.local.model.EventWithParentModel
import com.arkurl.eventtimepiece.data.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Locale

class EventViewModel(private val repository: EventRepository): ViewModel() {
    private val _parentEventList = MutableLiveData<List<Event>?>()
    private val _childEventList = MutableLiveData<List<EventWithParentModel>?>()
    private val _event = MutableLiveData<Event>()

    val event: LiveData<Event> get() = _event
    val parentEventList: LiveData<List<Event>?> get() = _parentEventList
    val childEventList: LiveData<List<EventWithParentModel>?> get() = _childEventList


    fun loadSpecifyEvent(id: Long) {
        viewModelScope.launch {
            val event = repository.querySpecifyEvent(id)
            _event.postValue(event)
        }
    }

    fun loadParentEvents() {
        viewModelScope.launch {
            val events = repository.queryParentEvents()
            _parentEventList.postValue(events)
        }
    }

    fun loadChildEventsByParentEventId(id: Long) {
        viewModelScope.launch {
            val events = repository.queryChildrenEventsByParentEventId(id)
            _childEventList.postValue(events)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.delete(event)
            refreshEvents(event.parentEventId)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repository.update(event)
            refreshEvents(event.parentEventId)
        }
    }

    fun updateEventById(id: Long, name: String, description: String) {
        viewModelScope.launch {
            val event = repository.querySpecifyEvent(id)
            repository.update(event.copy(name = name, description = description))
            refreshEvents(event.parentEventId)
        }
    }

    fun updateEventTimeCostById(id: Long, timeCost: Long) {
        viewModelScope.launch {
            val event = repository.querySpecifyEvent(id)
            repository.update(event.copy(timeCost = timeCost, updateTime = Instant.now()))
            refreshEvents(event.parentEventId)
        }
    }

    fun updateEventAndParentTimeCostById(id: Long, timeCost: Long) {
        viewModelScope.launch {
            val event = repository.querySpecifyEvent(id)
            repository.update(event.copy(timeCost = timeCost, updateTime = Instant.now()))
            if (event.parentEventId != null) {
                val parentEvent = repository.querySpecifyEvent(event.parentEventId)
                repository.update(parentEvent.copy(timeCost = parentEvent.timeCost + timeCost, updateTime = Instant.now()))
            }
            refreshEvents(event.parentEventId)
        }
    }

    fun insertEvent(event: Event) {
        viewModelScope.launch {
            repository.insert(event)
            refreshEvents(event.parentEventId)
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
            refreshEvents(parentEventId)
        }
    }

    private fun refreshEvents(parentEventId: Long?) {
        if (parentEventId == null) {
            return loadParentEvents()
        }
        loadChildEventsByParentEventId(parentEventId)
    }

    fun clearChildEvents() {
        _childEventList.postValue(null)
    }

    fun formatTimeCost(timeCost: Long): String {
        val hours = timeCost / 3600
        val minutes = (timeCost % 3600) / 60
        val seconds = timeCost % 60

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}