package com.arkurl.eventtimepiece.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.arkurl.eventtimepiece.data.local.entity.EventEntity


data class EventWithParent (
    @Embedded val eventEntity: EventEntity,
    @Relation(
        parentColumn = "parentEventId",
        entityColumn = "id"
    )
    val parentEventEntity: EventEntity?
)