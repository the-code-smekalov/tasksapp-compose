package com.romansmekalov.tasks.data.repository

import com.romansmekalov.tasks.data.local.TaskDao
import com.romansmekalov.tasks.data.local.toDomain
import com.romansmekalov.tasks.data.local.toEntity
import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val dao: TaskDao) : TaskRepository {

    override fun observeTasks(): Flow<Result<List<Task>>> =
        dao.observeAll().map { list -> Result.success(list.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }

    override suspend fun getTask(id: Long): Result<Task?> =
        runCatching { dao.getById(id)?.toDomain() }


    override suspend fun save(task: Task): Result<Long> = runCatching {
        val rowId = dao.upsert(task.toEntity())
        if (task.id == 0L) rowId else task.id
    }


    override suspend fun deleteTask(id: Long): Result<Boolean> = runCatching {
        dao.delete(id)
        true
    }
}
