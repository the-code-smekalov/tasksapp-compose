package com.romansmekalov.tasks.domain.usecase.attachment

import com.romansmekalov.tasks.domain.repository.AttachmentRepository
import javax.inject.Inject

class ImportAttachmentUseCase @Inject constructor(private val repository: AttachmentRepository) {
    suspend operator fun invoke(uri: String): Result<String> = repository.importFromUri(uri)
}