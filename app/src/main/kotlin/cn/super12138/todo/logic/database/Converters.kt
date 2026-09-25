package cn.super12138.todo.logic.database

import androidx.room3.ColumnTypeConverter
import kotlin.time.Instant

class Converters {
    @ColumnTypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(value) }
    }

    @ColumnTypeConverter
    fun timestampToInstant(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}