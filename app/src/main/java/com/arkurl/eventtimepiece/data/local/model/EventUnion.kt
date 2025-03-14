package com.arkurl.eventtimepiece.data.local.model

sealed class EventUnion {
    data class SingleEvent(val event: Event): EventUnion()
    data class RelatedEvent(val parent: Event, val child: Event): EventUnion()
}