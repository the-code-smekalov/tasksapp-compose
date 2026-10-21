package com.romansmekalov.tasks.presentation.feature.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.romansmekalov.tasks.domain.model.Task
import com.romansmekalov.tasks.domain.model.TaskStatus
import com.romansmekalov.tasks.presentation.R
import com.romansmekalov.tasks.presentation.common.PreviewApp
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListIntent
import com.romansmekalov.tasks.presentation.feature.tasklist.contract.TaskListState

@Composable
fun TaskListContentView(
    state: TaskListState.Content,
    onIntent: (TaskListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onClick = { onIntent(TaskListIntent.PressTask(task.id)) },
            )
        }
    }
}

@Composable
private fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(5.dp)
                .then(
                    Modifier.background(
                        when (task.status) {
                            TaskStatus.Completed -> Color.Green
                            TaskStatus.Active -> Color.Blue
                            else -> Color.Red
                        }
                    )
                )
        )
        Column(Modifier.padding(16.dp)) {
            Text(
                text = task.text.ifBlank { stringResource(R.string.empty_text) },
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            if (task.attachments.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.attachments_count, task.attachments.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun TaskListContentView_Preview() = PreviewApp {
    TaskListContentView(
        state = TaskListState.Content(
            listOf(
                Task(
                    id = 1,
                    text = "Моя первая задачка на сегодня будет оформлена в виде большого текста, который никогда не влезет в эту маааааленькую review (я на это очень сильно расчитываю)",
                    attachments = listOf("path1", "path2"),
                    status = TaskStatus.Active
                ), Task(
                    id = 2,
                    text = "Вторая задачка",
                    status = TaskStatus.Active
                ), Task(
                    id = 3,
                    text = "Задачка на подумать",
                    attachments = listOf("path3"),
                    status = TaskStatus.Completed
                )
            )
        ),
        onIntent = {},
    )
}