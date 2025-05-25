package com.arkurl.eventtimepiece.data.local.model

import com.arkurl.eventtimepiece.data.local.entity.EventEntity
import com.arkurl.eventtimepiece.data.local.entity.EventWithParentEntity

fun EventEntity.toModel(): Event {
    return Event (
        id = this.id,
        name = this.name,
        timeCost = this.timeCost,
        description = this.description,
        createTime = this.createTime,
        updateTime = this.updateTime,
        parentEventId = this.parentEventId
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = this.id,
        name = this.name,
        timeCost = this.timeCost,
        description = this.description,
        createTime = this.createTime,
        updateTime = this.updateTime,
        parentEventId = this.parentEventId,
    )
}

fun EventWithParentEntity.toModel(): EventWithParentModel {
    return EventWithParentModel(
        selfModel = this.eventEntity.toModel(),
        parentModel = this.parentEventEntity.toModel()
    )
}