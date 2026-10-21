package com.romansmekalov.tasks.presentation.feature.taskeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.romansmekalov.tasks.domain.model.MAX_ATTACHMENTS
import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.model.TaskStatus
import com.romansmekalov.tasks.domain.usecase.attachment.AttachCatUseCase
import com.romansmekalov.tasks.domain.usecase.attachment.DeleteAttachmentsUseCase
import com.romansmekalov.tasks.domain.usecase.attachment.ImportAttachmentUseCase
import com.romansmekalov.tasks.domain.usecase.task.DeleteTaskUseCase
import com.romansmekalov.tasks.domain.usecase.task.GetTaskUseCase
import com.romansmekalov.tasks.domain.usecase.task.SaveTaskUseCase
import com.romansmekalov.tasks.presentation.R
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorEffect
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorIntent
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorState
import com.romansmekalov.tasks.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTask: GetTaskUseCase,
    private val saveTask: SaveTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val importAttachment: ImportAttachmentUseCase,
    private val attachCat: AttachCatUseCase,
    private val deleteAttachments: DeleteAttachmentsUseCase,
) : ViewModel() {

    private companion object {
        const val NEW_ID = -1L
    }

    private val taskId: Long = savedStateHandle.toRoute<Routes.TaskEditor>().taskId ?: NEW_ID
    private val isNew = taskId == NEW_ID

    private val _state =
        MutableStateFlow(if (isNew) TaskEditorState.Content() else TaskEditorState.Loading)
    val state: StateFlow<TaskEditorState> = _state.asStateFlow()

    private val _effects = Channel<TaskEditorEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var original: Task? = null
    private val addedPaths = mutableListOf<String>()
    private val removedPaths = mutableListOf<String>()
    private var saved = false

    init {
        if (!isNew) loadTask()
    }

    fun onIntent(intent: TaskEditorIntent) {
        when (intent) {
            is TaskEditorIntent.TextChanged -> updateContent { it.copy(text = intent.text) }
            is TaskEditorIntent.FilesPicked -> importFiles(intent.uris)
            is TaskEditorIntent.RemoveAttachment -> removeAttachment(intent.path)
            TaskEditorIntent.AttachCat -> addCat()
            TaskEditorIntent.EnterEdit -> updateContent { it.copy(isEditing = true) }
            TaskEditorIntent.PressSave -> save()
            TaskEditorIntent.PressBack -> viewModelScope.launch { _effects.send(TaskEditorEffect.Back) }
            TaskEditorIntent.PressComplete -> complete()
            TaskEditorIntent.ToggleActionMenu -> toggleActionMenu()
            TaskEditorIntent.PressDelete -> delete()
            TaskEditorIntent.LaunchPicker -> viewModelScope.launch { _effects.send(TaskEditorEffect.LaunchPicker) }
        }
    }

    private fun loadTask() = viewModelScope.launch {
        _state.value = TaskEditorState.Content(isNew = isNew, isEditing = false)
        getTask(taskId)
            .onSuccess { task ->
                if (task == null) {
                    _effects.send(TaskEditorEffect.Message(R.string.err_not_found))
                    return@launch
                }

                original = task
                updateContent {
                    it.copy(
                        text = task.text,
                        attachments = task.attachments,
                        status = task.status,
                    )
                }

            }
            .onFailure {
                _state.value = TaskEditorState.Failure
            }

    }

    private fun importFiles(uris: List<String>) = viewModelScope.launch {
        val free = MAX_ATTACHMENTS - (_state.value as TaskEditorState.Content).attachments.size
        for (uri in uris.take(free.coerceAtLeast(0))) {
            importAttachment(uri)
                .onSuccess(::appendAttachment)
                .onFailure { _effects.send(TaskEditorEffect.Message(R.string.err_file)) }
        }
    }

    private fun addCat() {
        val s = _state.value as TaskEditorState.Content
        if (!s.canAttach || s.isCatLoading) return
        viewModelScope.launch {
            updateContent { it.copy(isCatLoading = true) }
            attachCat()
                .onSuccess(::appendAttachment)
                .onFailure { _effects.send(TaskEditorEffect.Message(R.string.err_cat)) }
            updateContent { it.copy(isCatLoading = false) }
        }
    }

    private fun appendAttachment(path: String) {
        addedPaths += path
        updateContent { it.copy(attachments = it.attachments + path) }
    }

    private fun removeAttachment(path: String) {
        updateContent { it.copy(attachments = it.attachments - path) }
        if (addedPaths.remove(path)) {
            viewModelScope.launch { deleteAttachments(listOf(path)) }
        } else {
            removedPaths += path
        }
    }

    private fun save() {
        val s = _state.value as TaskEditorState.Content
        updateContent { it.copy(saveInProcess = true) }
        if (!s.canSave || s.saveInProcess) return
        viewModelScope.launch {
            saveTask(
                Task(
                    id = if (isNew) 0 else taskId,
                    text = s.text.trim(),
                    attachments = s.attachments,
                    createdAt = original?.createdAt ?: System.currentTimeMillis(),
                    status = if (isNew) TaskStatus.Active else s.status,
                )
            )
                .onSuccess {
                    deleteAttachments(removedPaths)
                    saved = true
                    updateContent { it.copy(saveInProcess = false) }
                    _effects.send(TaskEditorEffect.Saved)
                }
                .onFailure { _effects.send(TaskEditorEffect.Message(R.string.err_save)) }
        }
    }

    private fun complete() {
        viewModelScope.launch {
            original?.let {
                saveTask(it.copy(status = TaskStatus.Completed))
                toggleActionMenu()
                _effects.send(TaskEditorEffect.Saved)
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            original?.let {
                deleteTask(it.id)
                toggleActionMenu()
                _effects.send(TaskEditorEffect.Back)
            }
        }
    }

    private fun toggleActionMenu() {
        updateContent { it.copy(menuExpanded = !it.menuExpanded) }
    }

    override fun onCleared() {
        if (!saved) {
            viewModelScope.launch { deleteAttachments(addedPaths) }
        }
    }

    private inline fun updateContent(crossinline block: (TaskEditorState.Content) -> TaskEditorState.Content) {
        _state.update { currentState ->
            if (currentState is TaskEditorState.Content) block(currentState) else currentState
        }
    }
}
