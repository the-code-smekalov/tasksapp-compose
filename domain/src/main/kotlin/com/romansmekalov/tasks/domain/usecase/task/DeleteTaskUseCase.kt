package com.romansmekalov.tasks.domain.usecase.task

import com.romansmekalov.tasks.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: Long): Result<Boolean> = repository.deleteTask(taskId)
}