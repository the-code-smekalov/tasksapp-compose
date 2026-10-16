package com.romansmekalov.tasks.domain.repository

interface AttachmentRepository {
    suspend fun importFromUri(uri: String): Result<String>
    suspend fun downloadRandomCat(): Result<String>
    fun delete(paths: List<String>)
}
