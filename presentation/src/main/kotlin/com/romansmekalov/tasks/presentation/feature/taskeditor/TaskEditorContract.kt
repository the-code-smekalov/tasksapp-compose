package com.romansmekalov.tasks.presentation.feature.taskeditor

import androidx.annotation.StringRes
import com.romansmekalov.tasks.domain.model.MAX_ATTACHMENTS
import com.romansmekalov.tasks.domain.model.TaskStatus

data class EditorState(
    val text: String = "",
    val attachments: List<String> = emptyList(),
    val isNew: Boolean = true,
    val isEditing: Boolean = true,
    val isLoading: Boolean = false,
    val isCatLoading: Boolean = false,
    val saveInProcess: Boolean = false,
    val status: TaskStatus = TaskStatus.Unknown,
    val menuExpanded: Boolean = false,
) {
    val canAttach: Boolean get() = attachments.size < MAX_ATTACHMENTS
    val canSave: Boolean get() = text.isNotBlank() || attachments.isNotEmpty()
}

sealed interface EditorIntent {
    data class TextChanged(val text: String) : EditorIntent
    data class FilesPicked(val uris: List<String>) : EditorIntent
    data class RemoveAttachment(val path: String) : EditorIntent
    data object AttachCat : EditorIntent
    data object EnterEdit : EditorIntent
    data object PressSave : EditorIntent
    data object PressBack : EditorIntent
    data object PressComplete : EditorIntent
    data object PressDelete : EditorIntent
    data object ToggleActionMenu : EditorIntent
}

sealed interface EditorEffect {
    data object Saved : EditorEffect
    data class Message(@StringRes val resId: Int) : EditorEffect
    data object Back : EditorEffect
}
