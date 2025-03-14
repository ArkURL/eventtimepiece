package com.arkurl.eventtimepiece.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithParentEntity (
    @Embedded val eventEntity: EventEntity,
    @Relation(
        parentColumn = "parentEventId",
        entityColumn = "id"
    )
    val parentEventEntity: EventEntity
)