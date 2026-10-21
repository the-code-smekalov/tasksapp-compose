package com.romansmekalov.tasks.data.di

import com.romansmekalov.tasks.data.repository.AttachmentRepositoryImpl
import com.romansmekalov.tasks.data.repository.TaskRepositoryImpl
import com.romansmekalov.tasks.domain.repository.AttachmentRepository
import com.romansmekalov.tasks.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds @Singleton
    abstract fun bindAttachmentRepository(impl: AttachmentRepositoryImpl): AttachmentRepository
}
