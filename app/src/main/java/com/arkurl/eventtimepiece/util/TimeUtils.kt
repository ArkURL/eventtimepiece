package com.arkurl.eventtimepiece.util

import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun formatTZDateTime(tzDateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")  // 自定义格式
        return tzDateTime.format(formatter)
    }

    fun formatInstantToReadableString(instant: Instant?): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")  // 自定义格式
        return instant?.atZone(java.time.ZoneId.systemDefault())?.format(formatter) ?: "Unknown"
    }
}