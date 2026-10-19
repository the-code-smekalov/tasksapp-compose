package com.romansmekalov.tasks.domain.model

const val MAX_ATTACHMENTS = 5

data class Task(
    val id: Long = 0,
    val text: String,
    val attachments: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val status: TaskStatus,
)
