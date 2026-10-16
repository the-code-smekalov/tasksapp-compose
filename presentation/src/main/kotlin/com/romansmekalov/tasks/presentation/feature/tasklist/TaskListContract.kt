package com.romansmekalov.tasks.presentation.feature.tasklist

sealed interface TaskListIntent {
    data class PressTask(val taskId: Long) : TaskListIntent
    data object PressAdd : TaskListIntent
}

sealed interface TaskListEffect {
    data object OpenCreator : TaskListEffect
    data class OpenEditor(val taskId: Long) : TaskListEffect
}
