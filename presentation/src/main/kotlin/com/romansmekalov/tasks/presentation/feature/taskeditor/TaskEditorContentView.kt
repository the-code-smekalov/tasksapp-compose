package com.romansmekalov.tasks.presentation.feature.taskeditor

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.romansmekalov.tasks.domain.model.MAX_ATTACHMENTS
import com.romansmekalov.tasks.presentation.R
import com.romansmekalov.tasks.presentation.common.PreviewApp
import com.romansmekalov.tasks.presentation.components.AttachmentPreview
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorIntent
import com.romansmekalov.tasks.presentation.feature.taskeditor.contract.TaskEditorState

@Composable
fun TaskEditorContentView(
    state: TaskEditorState.Content,
    onIntent: (TaskEditorIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    OutlinedTextField(
        value = state.text,
        onValueChange = { onIntent(TaskEditorIntent.TextChanged(it)) },
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
                onClick = { onIntent(TaskEditorIntent.LaunchPicker) },
                enabled = state.canAttach,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.attach_file))
            }

            Button(
                onClick = { onIntent(TaskEditorIntent.AttachCat) },
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
                        { onIntent(TaskEditorIntent.RemoveAttachment(path)) }
                    } else null
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TaskEditorContentView_Preview() = PreviewApp {
    TaskEditorContentView(
        state = TaskEditorState.Content(
            text = "Моя первая задачка на сегодня будет оформлена в виде большого текста, который никогда не влезет в эту маааааленькую review (я на это очень сильно расчитываю)",
            attachments = listOf("path/image.jpg", "path/fil.zip", "path/video.mp4")
        ),
        onIntent = {},
    )
}

@PreviewLightDark
@Composable
private fun TaskEditorContentView_LoadingCatPreview() = PreviewApp {
    TaskEditorContentView(
        state = TaskEditorState.Content(
            text = "Моя первая задачка на сегодня будет оформлена в виде большого текста, который никогда не влезет в эту маааааленькую review (я на это очень сильно расчитываю)",
            isCatLoading = true,
        ),
        onIntent = {},
    )
}

@PreviewLightDark
@Composable
private fun TaskEditorContentView_OnlyViewPreview() = PreviewApp {
    TaskEditorContentView(
        state = TaskEditorState.Content(
            text = "Моя первая задачка на сегодня будет оформлена в виде большого текста, который никогда не влезет в эту маааааленькую review (я на это очень сильно расчитываю)",
            attachments = listOf("path/image.jpg", "path/fil.zip", "path/video.mp4"),
            isEditing = false,
        ),
        onIntent = {},
    )
}
