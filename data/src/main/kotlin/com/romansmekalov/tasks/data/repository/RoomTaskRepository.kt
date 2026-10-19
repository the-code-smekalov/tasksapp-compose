package com.romansmekalov.tasks.data.repository

import com.romansmekalov.tasks.data.local.TaskDao
import com.romansmekalov.tasks.data.local.toDomain
import com.romansmekalov.tasks.data.local.toEntity
import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomTaskRepository @Inject constructor(private val dao: TaskDao) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getTask(id: Long): Task? = dao.getById(id)?.toDomain()

    override suspend fun save(task: Task): Long {
        val rowId = dao.upsert(task.toEntity())
        return if (task.id == 0L) rowId else task.id
    }

    override suspend fun deleteTask(id: Long) = dao.delete(id)
}
