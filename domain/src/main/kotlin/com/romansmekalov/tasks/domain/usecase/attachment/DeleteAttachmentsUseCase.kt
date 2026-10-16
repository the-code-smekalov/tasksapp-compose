package com.romansmekalov.tasks.domain.usecase.attachment

import com.romansmekalov.tasks.domain.repository.AttachmentRepository
import javax.inject.Inject

class DeleteAttachmentsUseCase @Inject constructor(private val repository: AttachmentRepository) {
    operator fun invoke(paths: List<String>) = repository.delete(paths)
}