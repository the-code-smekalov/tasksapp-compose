package com.romansmekalov.tasks.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.romansmekalov.tasks.data.di.IODispatcher
import com.romansmekalov.tasks.data.remote.CatApi
import com.romansmekalov.tasks.data.remote.InternalApi
import com.romansmekalov.tasks.domain.repository.AttachmentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val catApi: CatApi,
    private val internalApi: InternalApi,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : AttachmentRepository {

    private val dir: File
        get() = File(context.filesDir, "attachments").apply { mkdirs() }

    override suspend fun importFromUri(uri: String): Result<String> = withContext(ioDispatcher) {
        runCatching {
            val source = uri.toUri()
            val name = queryDisplayName(source) ?: "file"
            val target = File(dir, "${UUID.randomUUID()}_${sanitize(name)}")
            try {
                val input =
                    requireNotNull(context.contentResolver.openInputStream(source)) { "Cannot open $uri" }
                input.use { i -> target.outputStream().use { o -> i.copyTo(o) } }
            } catch (e: Exception) {
                target.delete(); throw e
            }
            target.absolutePath
        }
    }

    override suspend fun downloadRandomCat(): Result<String> = withContext(ioDispatcher) {
        runCatching {
            val image = catApi.randomImage().first()
            val ext = image.url.substringAfterLast('.', "jpg").take(4)
            val target = File(dir, "${UUID.randomUUID()}_cat.$ext")
            try {
                internalApi.download(image.url).byteStream()
                    .use { i -> target.outputStream().use { o -> i.copyTo(o) } }

            } catch (e: Exception) {
                target.delete(); throw e
            }
            target.absolutePath
        }
    }

    override suspend fun delete(paths: List<String>): Result<Boolean> =
        withContext(ioDispatcher) {
            runCatching {
                paths.forEach { File(it).delete() }
                true
            }
        }

    private fun queryDisplayName(uri: Uri): String? =
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { c -> if (c.moveToFirst()) c.getString(0) else null }

    private fun sanitize(name: String) = name.replace(Regex("[^\\p{L}\\p{N}._-]"), "_")
}
