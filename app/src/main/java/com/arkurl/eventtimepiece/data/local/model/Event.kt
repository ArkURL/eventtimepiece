package com.arkurl.eventtimepiece.data.local.model

import java.time.Instant

data class Event(
    val id: Long,
    val name: String,
    val timeCost: Long,
    val description: String?,
    val createTime: Instant,
    val updateTime: Instant,
    val parentEventId: Long?
)