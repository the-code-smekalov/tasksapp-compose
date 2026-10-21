package com.romansmekalov.tasks.presentation.feature.taskeditor.contract

import com.romansmekalov.tasks.domain.model.MAX_ATTACHMENTS
import com.romansmekalov.tasks.domain.model.TaskStatus

sealed class TaskEditorState {
    data object Loading : TaskEditorState()
    data class Content(
        val text: String = "",
        val attachments: List<String> = emptyList(),
        val isNew: Boolean = true,
        val isEditing: Boolean = true,
        val isCatLoading: Boolean = false,
        val saveInProcess: Boolean = false,
        val status: TaskStatus = TaskStatus.Unknown,
        val menuExpanded: Boolean = false,
    ) : TaskEditorState() {
        val canAttach: Boolean get() = attachments.size < MAX_ATTACHMENTS
        val canSave: Boolean get() = text.isNotBlank() || attachments.isNotEmpty()
    }

    data object Failure : TaskEditorState()
}