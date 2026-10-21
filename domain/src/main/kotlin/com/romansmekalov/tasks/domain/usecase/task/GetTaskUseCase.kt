package com.romansmekalov.tasks.domain.usecase.task

import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long): Result<Task?> = repository.getTask(id)
}