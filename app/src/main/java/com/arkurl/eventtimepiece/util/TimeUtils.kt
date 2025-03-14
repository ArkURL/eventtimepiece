package com.arkurl.eventtimepiece.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {
    fun formatTZDateTime(tzDateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")  // 自定义格式
        return tzDateTime.format(formatter)
    }

    fun formatInstant(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            .withZone(ZoneId.systemDefault()) // 使用当前系统时区

        return formatter.format(instant)
    }

    fun formatTimeCost(timeCost: Long): String {
        val hours = timeCost / 3600
        val minutes = (timeCost % 3600) / 60
        val seconds = timeCost % 60

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}