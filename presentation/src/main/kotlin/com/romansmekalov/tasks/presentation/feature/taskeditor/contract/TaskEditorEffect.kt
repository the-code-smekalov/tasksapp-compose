package com.romansmekalov.tasks.presentation.feature.taskeditor.contract

import androidx.annotation.StringRes

sealed interface TaskEditorEffect {
    data object Saved : TaskEditorEffect
    data class Message(@param:StringRes val resId: Int) : TaskEditorEffect
    data object Back : TaskEditorEffect
    data object LaunchPicker : TaskEditorEffect
}