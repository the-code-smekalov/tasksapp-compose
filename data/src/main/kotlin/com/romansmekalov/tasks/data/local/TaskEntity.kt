package com.romansmekalov.tasks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.romansmekalov.tasks.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val attachments: List<String>,
    val createdAt: Long,
)

fun TaskEntity.toDomain() = Task(id, text, attachments, createdAt)
fun Task.toEntity() = TaskEntity(id, text, attachments, createdAt)
