package com.romansmekalov.tasks.presentation.feature.tasklist.contract

sealed interface TaskListEffect {
    data object OpenCreator : TaskListEffect
    data class OpenEditor(val taskId: Long) : TaskListEffect
}