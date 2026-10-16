package com.romansmekalov.tasks.presentation.feature.taskeditor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.romansmekalov.tasks.domain.model.MAX_ATTACHMENTS
import com.romansmekalov.tasks.presentation.R
import com.romansmekalov.tasks.presentation.components.AttachmentPreview
import com.romansmekalov.tasks.presentation.navigation.LocalNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(viewModel: TaskEditorViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val nc = LocalNavHost.current

    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) viewModel.onIntent(EditorIntent.FilesPicked(uris.map { it.toString() }))
        }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                EditorEffect.Saved, EditorEffect.Back -> nc.popBackStack()
                is EditorEffect.Message -> snackbar.showSnackbar(context.getString(effect.resId))
            }
        }
    }

    val title = when {
        state.isNew -> R.string.title_new
        state.isEditing -> R.string.title_edit
        else -> R.string.title_view
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onIntent(EditorIntent.PressBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(
                            onClick = { viewModel.onIntent(EditorIntent.Save) },
                            enabled = state.canSave,
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = stringResource(R.string.save),
                            )
                        }
                    } else if (!state.isLoading) {
                        IconButton(onClick = { viewModel.onIntent(EditorIntent.EnterEdit) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit),
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.text,
                onValueChange = { viewModel.onIntent(EditorIntent.TextChanged(it)) },
                readOnly = !state.isEditing,
                minLines = 5,
                label = { Text(stringResource(R.string.text_hint)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(
                    R.string.files_count,
                    state.attachments.size,
                    MAX_ATTACHMENTS
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.isEditing) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { picker.launch(arrayOf("*/*")) },
                        enabled = state.canAttach,
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.attach_file)) }

                    Button(
                        onClick = { viewModel.onIntent(EditorIntent.AttachCat) },
                        enabled = state.canAttach && !state.isCatLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (state.isCatLoading) CircularProgressIndicator(
                            Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        else Text(stringResource(R.string.attach_cat))
                    }
                }
            }

            if (state.attachments.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.attachments, key = { it }) { path ->
                        AttachmentPreview(
                            path = path,
                            onRemove = if (state.isEditing) {
                                { viewModel.onIntent(EditorIntent.RemoveAttachment(path)) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}
