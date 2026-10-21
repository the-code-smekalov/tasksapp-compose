package com.romansmekalov.tasks.presentation.feature.tasklist.contract

sealed interface TaskListIntent {
    data class PressTask(val taskId: Long) : TaskListIntent
    data object PressAdd : TaskListIntent
}