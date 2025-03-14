package com.arkurl.eventtimepiece.data.repository

import android.content.Context
import com.arkurl.eventtimepiece.data.local.dao.EventDao
import com.arkurl.eventtimepiece.data.local.database.AppDatabase
import com.arkurl.eventtimepiece.data.local.model.Event
import com.arkurl.eventtimepiece.data.local.model.EventWithParentModel
import com.arkurl.eventtimepiece.data.local.model.toEntity
import com.arkurl.eventtimepiece.data.local.model.toModel

class EventRepository(private val eventDao: EventDao) {
    companion object {
        @Volatile private var instance: EventRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: EventRepository(AppDatabase.getDatabaseInstance(context).eventDao()).also { instance = it }
        }
    }

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

    suspend fun queryParentEvents(): List<Event>? {
        return eventDao.queryParentEvents()?.map { it.toModel() }
    }

    suspend fun queryChildrenEventsByParentEventId(parentEventId: Long): List<EventWithParentModel>? {
        return eventDao.queryChildrenEventsByParentId(parentEventId)?.map { it.toModel() }
    }

    suspend fun querySpecifyEvent(id: Long): Event {
        return eventDao.querySpecifyEvent(id).toModel()
    }
}