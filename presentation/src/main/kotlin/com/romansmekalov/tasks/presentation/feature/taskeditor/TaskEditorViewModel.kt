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

    private val taskId: Long = savedStateHandle.toRoute<Routes.TaskEditor>().taskId ?: NEW_ID
    private val isNew = taskId == NEW_ID

    private val _state =
        MutableStateFlow(EditorState(isNew = isNew, isEditing = isNew, isLoading = !isNew))
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val _effects = Channel<EditorEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var original: Task? = null
    private val addedPaths = mutableListOf<String>()
    private val removedPaths = mutableListOf<String>()
    private var saved = false

    init {
        if (!isNew) loadTask()
    }

    fun onIntent(intent: EditorIntent) {
        when (intent) {
            is EditorIntent.TextChanged -> _state.update { it.copy(text = intent.text) }
            is EditorIntent.FilesPicked -> importFiles(intent.uris)
            is EditorIntent.RemoveAttachment -> removeAttachment(intent.path)
            EditorIntent.AttachCat -> addCat()
            EditorIntent.EnterEdit -> _state.update { it.copy(isEditing = true) }
            EditorIntent.PressSave -> save()
            EditorIntent.PressBack -> viewModelScope.launch { _effects.send(EditorEffect.Back) }
            EditorIntent.PressComplete -> complete()
            EditorIntent.ToggleActionMenu -> _state.update { it.copy(menuExpanded = !it.menuExpanded) }
            EditorIntent.PressDelete -> delete()
        }
    }

    private fun loadTask() = viewModelScope.launch {
        val task = getTask(taskId)
        if (task == null) {
            _effects.send(EditorEffect.Message(R.string.err_not_found))
            _state.update { it.copy(isLoading = false) }
            return@launch
        }
        original = task
        _state.update {
            it.copy(
                text = task.text,
                attachments = task.attachments,
                isLoading = false,
                status = task.status,
            )
        }
    }

    private fun importFiles(uris: List<String>) = viewModelScope.launch {
        val free = MAX_ATTACHMENTS - _state.value.attachments.size
        for (uri in uris.take(free.coerceAtLeast(0))) {
            importAttachment(uri)
                .onSuccess(::appendAttachment)
                .onFailure { _effects.send(EditorEffect.Message(R.string.err_file)) }
        }
    }

    private fun addCat() {
        val s = _state.value
        if (!s.canAttach || s.isCatLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isCatLoading = true) }
            attachCat()
                .onSuccess(::appendAttachment)
                .onFailure { _effects.send(EditorEffect.Message(R.string.err_cat)) }
            _state.update { it.copy(isCatLoading = false) }
        }
    }

    private fun appendAttachment(path: String) {
        addedPaths += path
        _state.update { it.copy(attachments = it.attachments + path) }
    }

    private fun removeAttachment(path: String) {
        _state.update { it.copy(attachments = it.attachments - path) }
        if (addedPaths.remove(path)) deleteAttachments(listOf(path)) else removedPaths += path
    }

    private fun save() {
        _state.update { it.copy(saveInProcess = true) }
        val s = _state.value
        if (!s.canSave) return
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
            deleteAttachments(removedPaths)
            saved = true
            _state.update { it.copy(saveInProcess = false) }
            _effects.send(EditorEffect.Saved)
        }
    }

    private fun complete() {
        viewModelScope.launch {
            original?.let {
                saveTask(it.copy(status = TaskStatus.Completed))
                _effects.send(EditorEffect.Saved)
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            original?.let {
                deleteTask(it.id)
                _effects.send(EditorEffect.Back)
            }
        }
    }

    override fun onCleared() {
        if (!saved) deleteAttachments(addedPaths)
        super.onCleared()
    }

    private companion object {
        const val NEW_ID = -1L
    }
}
