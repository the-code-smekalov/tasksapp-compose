package com.romansmekalov.tasks.presentation.feature.taskeditor.contract

sealed interface TaskEditorIntent {
    data class TextChanged(val text: String) : TaskEditorIntent
    data class FilesPicked(val uris: List<String>) : TaskEditorIntent
    data class RemoveAttachment(val path: String) : TaskEditorIntent
    data object AttachCat : TaskEditorIntent
    data object EnterEdit : TaskEditorIntent
    data object PressSave : TaskEditorIntent
    data object PressBack : TaskEditorIntent
    data object PressComplete : TaskEditorIntent
    data object PressDelete : TaskEditorIntent
    data object ToggleActionMenu : TaskEditorIntent
    data object LaunchPicker : TaskEditorIntent
}