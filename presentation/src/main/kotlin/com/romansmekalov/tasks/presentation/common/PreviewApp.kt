package com.romansmekalov.tasks.presentation.common

import androidx.compose.runtime.Composable
import com.romansmekalov.tasks.presentation.theme.TasksTheme

@Composable
fun PreviewApp(content: @Composable () -> Unit) {
    TasksTheme { content() }
}