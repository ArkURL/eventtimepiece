package com.arkurl.eventtimepiece.data.local.model

import java.time.Instant

data class EventWithParentModel(
    val parentId: Long?,
    val parentEventName: String?,
    val id: Long,
    val eventName: String,
    val eventDescription: String?,
    val eventCreateTime: Instant,
    val eventUpdateTime: Instant,
    val timeCost: Long,
    val selfModel: Event,
    val parentModel: Event
)