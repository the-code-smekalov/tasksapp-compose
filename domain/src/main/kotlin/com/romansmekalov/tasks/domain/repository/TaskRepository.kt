package com.romansmekalov.tasks.domain.repository

import com.romansmekalov.tasks.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun getTask(id: Long): Task?
    suspend fun save(task: Task): Long
}
