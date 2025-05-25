package com.arkurl.eventtimepiece.data.local.model

data class EventWithParentModel(
    val selfModel: Event,
    val parentModel: Event
)