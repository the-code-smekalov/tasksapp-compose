package com.romansmekalov.tasks.domain.usecase.attachment

import com.romansmekalov.tasks.domain.repository.AttachmentRepository
import javax.inject.Inject

class AttachCatUseCase @Inject constructor(private val repository: AttachmentRepository) {
    suspend operator fun invoke(): Result<String> = repository.downloadRandomCat()
}