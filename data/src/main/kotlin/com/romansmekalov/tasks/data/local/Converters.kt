package com.romansmekalov.tasks.data.local

import androidx.room.TypeConverter
import com.romansmekalov.tasks.domain.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString("\n")

    @TypeConverter
    fun toList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split("\n")

    @TypeConverter
    fun fromStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): TaskStatus {
        return try {
            TaskStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TaskStatus.Unknown
        }
    }
}
