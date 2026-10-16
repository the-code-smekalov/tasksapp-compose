package com.romansmekalov.tasks.domain.usecase.task

import com.romansmekalov.tasks.domain.repository.TaskRepository
import javax.inject.Inject

class ObserveTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke() = repository.observeTasks()
}