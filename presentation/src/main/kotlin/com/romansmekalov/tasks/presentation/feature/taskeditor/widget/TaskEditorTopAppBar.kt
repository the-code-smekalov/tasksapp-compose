package com.romansmekalov.tasks.presentation.feature.taskeditor.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.romansmekalov.tasks.domain.model.TaskStatus
import com.romansmekalov.tasks.presentation.R
import com.romansmekalov.tasks.presentation.common.PreviewApp
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorIntent
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorTopAppBar(state: TaskEditorState, onIntent: (TaskEditorIntent) -> Unit) {
    val title = when (state) {
        is TaskEditorState.Content if state.isNew -> R.string.title_new
        is TaskEditorState.Content if state.isEditing -> R.string.title_edit
        else -> R.string.title_view
    }
    TopAppBar(
        title = { Text(stringResource(title)) },
        navigationIcon = {
            IconButton(onClick = { onIntent(TaskEditorIntent.PressBack) }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        actions = {
            (state as? TaskEditorState.Content)?.let {
                if (it.isEditing) {
                    IconButton(
                        onClick = { onIntent(TaskEditorIntent.PressSave) },
                        enabled = it.canSave && !it.saveInProcess,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                        )
                    }
                } else if(!it.isNew) {
                    IconButton(onClick = { onIntent(TaskEditorIntent.EnterEdit) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                        )
                    }
                }
                if (!it.isNew) {
                    IconButton(onClick = { onIntent(TaskEditorIntent.ToggleActionMenu) }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                    DropdownMenu(
                        expanded = it.menuExpanded,
                        onDismissRequest = { onIntent(TaskEditorIntent.ToggleActionMenu) },
                    ) {
                        if (it.status == TaskStatus.Active) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = stringResource(R.string.complete),
                                    )
                                },
                                text = { Text(stringResource(R.string.complete)) },
                                onClick = {
                                    onIntent(TaskEditorIntent.PressComplete)
                                },
                            )
                        }
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                )
                            },
                            text = { Text(stringResource(R.string.delete)) },
                            colors = MenuDefaults.itemColors()
                                .copy(leadingIconColor = MaterialTheme.colorScheme.error),
                            onClick = {
                                onIntent(TaskEditorIntent.PressDelete)
                            },
                        )
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun TaskEditorTopAppBar_NewPreview() = PreviewApp {
    TaskEditorTopAppBar(TaskEditorState.Content()) { }
}

@PreviewLightDark
@Composable
private fun TaskEditorTopAppBar_isEditingPreview() = PreviewApp {
    TaskEditorTopAppBar(TaskEditorState.Content(isNew = false, isEditing = true)) { }
}

@PreviewLightDark
@Composable
private fun TaskEditorTopAppBar_noEditingPreview() = PreviewApp {
    TaskEditorTopAppBar(TaskEditorState.Content(isNew = false, isEditing = false)) { }
}

@PreviewLightDark
@Composable
private fun TaskEditorTopAppBar_menuExpandedPreview() = PreviewApp {
    Box(Modifier
        .fillMaxWidth()
        .height(200.dp)) {
        TaskEditorTopAppBar(
            TaskEditorState.Content(
                isNew = false,
                isEditing = false,
                menuExpanded = true
            )
        ) { }
    }
}

@PreviewLightDark
@Composable
private fun TaskEditorTopAppBar_menuExpandedActivePreview() = PreviewApp {
    Box(Modifier
        .fillMaxWidth()
        .height(250.dp)) {
        TaskEditorTopAppBar(
            TaskEditorState.Content(
                isNew = false,
                isEditing = false,
                menuExpanded = true,
                status = TaskStatus.Active,
            )
        ) { }
    }
}