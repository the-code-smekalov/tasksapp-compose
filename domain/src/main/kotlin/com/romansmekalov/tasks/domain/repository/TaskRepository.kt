package com.romansmekalov.tasks.domain.repository

import com.romansmekalov.tasks.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<Result<List<Task>>>
    suspend fun getTask(id: Long): Result<Task?>
    suspend fun save(task: Task): Result<Long>
    suspend fun deleteTask(id: Long): Result<Boolean>
}
