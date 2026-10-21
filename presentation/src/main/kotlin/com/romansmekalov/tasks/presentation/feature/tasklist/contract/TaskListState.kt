package com.romansmekalov.tasks.presentation.feature.tasklist.contract

import com.romansmekalov.tasks.domain.model.Task

sealed class TaskListState {
    data object Loading : TaskListState()
    data class Content(val tasks: List<Task>) : TaskListState()
    data object Empty : TaskListState()
    data object Failure : TaskListState()
}