package com.arkurl.eventtimepiece.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "event",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentEventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EventEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val timeCost: Long = 0,
    val description: String? = null,
    val createTime: Instant = Instant.now(),
    val updateTime: Instant = Instant.now(),
    val parentEventId: Long? = null
)