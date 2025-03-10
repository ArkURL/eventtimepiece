package com.arkurl.eventtimepiece.data.repository

import com.arkurl.eventtimepiece.data.local.dao.EventDao
import com.arkurl.eventtimepiece.data.local.model.Event
import com.arkurl.eventtimepiece.data.local.model.toEntity
import com.arkurl.eventtimepiece.data.local.model.toModel

class EventRepository(private val eventDao: EventDao) {
    suspend fun insert(vararg events: Event) {
        eventDao.insert(*events.map { it.toEntity() }.toTypedArray())
    }

    suspend fun delete(vararg events: Event) {
        eventDao.delete(*events.map { it.toEntity() }.toTypedArray())
    }

    suspend fun update(vararg events: Event) {
        eventDao.update(*events.map { it.toEntity() }.toTypedArray())
    }

    suspend fun queryAllEvents(): List<Event>? {
        return eventDao.queryAllEvents()?.map { it.toModel() }
    }

    suspend fun querySpecifyEvent(id: Int): Event? {
        return eventDao.querySpecifyEvent(id)?.toModel()
    }
}