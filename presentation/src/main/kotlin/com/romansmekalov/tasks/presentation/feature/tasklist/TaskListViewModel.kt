package com.romansmekalov.tasks.presentation.feature.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.usecase.task.ObserveTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListState(val tasks: List<Task> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class TaskListViewModel @Inject constructor(observeTasks: ObserveTasksUseCase) : ViewModel() {
    val state: StateFlow<TaskListState> = observeTasks()
        .map { TaskListState(tasks = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskListState()
        )

    private val _effects = Channel<TaskListEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: TaskListIntent) {
        when (intent) {
            is TaskListIntent.PressTask -> viewModelScope.launch {
                _effects.send(TaskListEffect.OpenEditor(intent.taskId))
            }

            TaskListIntent.PressAdd -> viewModelScope.launch {
                _effects.send(TaskListEffect.OpenCreator)
            }
        }
    }
}
