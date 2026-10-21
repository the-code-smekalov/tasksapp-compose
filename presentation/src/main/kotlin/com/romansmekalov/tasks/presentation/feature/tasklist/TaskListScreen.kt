package com.romansmekalov.tasks.presentation.feature.tasklist

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.romansmekalov.tasks.presentation.R
import com.romansmekalov.tasks.presentation.components.LoadingView
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListEffect
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListIntent
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListState
import com.romansmekalov.tasks.presentation.navigation.LocalNavHost
import com.romansmekalov.tasks.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskListViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val nc = LocalNavHost.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TaskListEffect.OpenCreator -> nc.navigate(Routes.TaskEditor())
                is TaskListEffect.OpenEditor -> nc.navigate(Routes.TaskEditor(effect.taskId))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tasks_title)) },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(TaskListIntent.PressAdd) }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_task)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        when (val viewState = state) {
            TaskListState.Loading -> LoadingView()
            TaskListState.Empty -> TaskListEmptyView(Modifier.padding(innerPadding))
            is TaskListState.Content -> TaskListContentView(
                viewState,
                viewModel::onIntent,
                Modifier.padding(innerPadding),
            )

            TaskListState.Failure -> TaskListFailureView(Modifier.padding(innerPadding))
        }
    }
}