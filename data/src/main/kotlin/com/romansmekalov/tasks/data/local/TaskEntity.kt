package com.romansmekalov.tasks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val attachments: List<String>,
    val createdAt: Long,
    val status: TaskStatus,
)

fun TaskEntity.toDomain() = Task(id, text, attachments, createdAt, status)
fun Task.toEntity() = TaskEntity(id, text, attachments, createdAt, status)
