package com.romansmekalov.tasks.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.romansmekalov.tasks.presentation.common.PreviewApp

@Composable
fun LoadingView() = Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator(strokeWidth = 2.dp)
}

@Composable
@Preview
private fun LoadingView_Preview() = PreviewApp { LoadingView() }