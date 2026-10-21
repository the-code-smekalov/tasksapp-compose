package com.romansmekalov.tasks.presentation.feature.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romansmekalov.tasks.domain.usecase.task.ObserveTasksUseCase
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListEffect
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListIntent
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(observeTasks: ObserveTasksUseCase) : ViewModel() {
    val state: StateFlow<TaskListState> = observeTasks()
        .map { result ->
            result.fold(
                onSuccess = {
                    if (it.isEmpty()) TaskListState.Empty else TaskListState.Content(tasks = it)
                },
                onFailure = { TaskListState.Failure },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskListState.Loading
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
