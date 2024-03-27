package com.example.stations.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
        .toFormatter()
    return LocalDateTime.parse(this, formatter)
}

fun LocalDateTime.toIsoString(): String {
    val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
        .toFormatter()
    return formatter.format(this)
}