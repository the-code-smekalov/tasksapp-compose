package com.romansmekalov.tasks.presentation.feature.taskeditor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.romansmekalov.tasks.presentation.components.LoadingView
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorEffect
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorIntent
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorState
import com.romansmekalov.tasks.presentation.feature.taskeditor.widget.TaskEditorTopAppBar
import com.romansmekalov.tasks.presentation.navigation.LocalNavHost

@Composable
fun TaskEditorScreen(viewModel: TaskEditorViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val nc = LocalNavHost.current

    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) viewModel.onIntent(TaskEditorIntent.FilesPicked(uris.map { it.toString() }))
        }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TaskEditorEffect.Saved, TaskEditorEffect.Back -> nc.popBackStack()
                is TaskEditorEffect.Message -> {
                    snackbar.showSnackbar(resources.getString(effect.resId))
                }

                TaskEditorEffect.LaunchPicker -> picker.launch(arrayOf("*/*"))
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TaskEditorTopAppBar(state, viewModel::onIntent) }
    ) { innerPadding ->
        when (val viewState = state) {
            TaskEditorState.Loading -> LoadingView()
            is TaskEditorState.Content -> TaskEditorContentView(
                viewState,
                viewModel::onIntent,
                Modifier.padding(innerPadding)
            )

            TaskEditorState.Failure -> TaskEditorFailureView(Modifier.padding(innerPadding))
        }
    }
}
