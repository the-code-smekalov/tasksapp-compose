package com.romansmekalov.tasks.data.di

import android.content.Context
import androidx.room.Room
import com.romansmekalov.tasks.data.local.AppDatabase
import com.romansmekalov.tasks.data.local.TaskDao
import com.romansmekalov.tasks.data.local.migration.Migration_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "tasks.db")
            .addMigrations(Migration_1_2)
            .build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}
