package com.romansmekalov.tasks.domain.repository

interface AttachmentRepository {
    suspend fun importFromUri(uri: String): Result<String>
    suspend fun downloadRandomCat(): Result<String>
    suspend fun delete(paths: List<String>): Result<Boolean>
}
