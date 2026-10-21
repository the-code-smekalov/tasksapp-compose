package com.romansmekalov.tasks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.romansmekalov.tasks.presentation.feature.taskeditor.TaskEditorScreen
import com.romansmekalov.tasks.presentation.feature.taskeditor.TaskEditorViewModel
import com.romansmekalov.tasks.presentation.feature.tasklist.TaskListScreen
import com.romansmekalov.tasks.presentation.feature.tasklist.TaskListViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {
    @Serializable
    data object Tasks : Routes

    @Serializable
    data class TaskEditor(val taskId: Long? = null) : Routes
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavHost provides navController) {
        NavHost(navController = navController, startDestination = Routes.Tasks) {
            composable<Routes.Tasks> {
                val vm: TaskListViewModel = hiltViewModel()
                TaskListScreen(vm)
            }
            composable<Routes.TaskEditor> {
                val vm: TaskEditorViewModel = hiltViewModel()
                TaskEditorScreen(vm)
            }
        }
    }
}
