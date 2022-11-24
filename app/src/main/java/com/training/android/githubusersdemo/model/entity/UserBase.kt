package com.training.android.githubusersdemo.model.entity

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val DATE_TIME_PATTERN_TO_PARSE = "yyyy-MM-dd\'T\'HH:mm:ssz"
private const val DATE_PATTERN_TO_DISPLAY = "dd.MM.yyyy"

open class UserBase(
    open var createdAt: String?,
) {
    private val createdAtDateTime: LocalDateTime?
        get() = createdAt?.let { datetimeString ->
            try {
                val formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_TO_PARSE)
                LocalDateTime.parse(datetimeString, formatter)
            } catch (ex: DateTimeParseException) {
                null
            }
        }

    val createdAtDate: String?
        get() = createdAtDateTime?.let { datetime ->
            try {
                val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_TO_DISPLAY)
                datetime.format(formatter)
            } catch (ex: DateTimeParseException) {
                null
            }
        }

    val hasDetails: Boolean
        get() = createdAt != null
}