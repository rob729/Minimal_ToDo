package com.example.robin.roomwordsample.di

import android.content.Context
import androidx.room.Room
import com.example.robin.roomwordsample.data.TaskDao
import com.example.robin.roomwordsample.data.TaskRepository
import com.example.robin.roomwordsample.data.TaskRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TaskRoomDatabase{
        return Room.databaseBuilder(context, TaskRoomDatabase::class.java,
            "Word_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideObjectDao(database: TaskRoomDatabase): TaskDao {
        return database.wordDao()
    }

    @Provides
    fun provideRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }
}