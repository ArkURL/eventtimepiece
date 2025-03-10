package com.arkurl.eventtimepiece.data.local.database

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun timestampToInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}