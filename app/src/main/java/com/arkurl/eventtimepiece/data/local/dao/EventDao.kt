package com.arkurl.eventtimepiece.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.arkurl.eventtimepiece.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Insert
    suspend fun insert(vararg eventEntity: EventEntity)

    @Delete
    suspend fun delete(vararg eventEntity: EventEntity)

    @Update
    suspend fun update(vararg eventEntity: EventEntity)

    @Query("SELECT * FROM event")
    suspend fun queryAllEvents(): List<EventEntity>?

    @Query("SELECT * FROM event WHERE id = :id")
    suspend fun querySpecifyEvent(id: Int): EventEntity?

    @Query("SELECT * FROM event WHERE parentEventId = :parentEventId")
    suspend fun queryChildEvents(parentEventId: Int): List<EventEntity>?

}