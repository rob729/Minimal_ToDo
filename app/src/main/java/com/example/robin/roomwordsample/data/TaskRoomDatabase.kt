package com.example.robin.roomwordsample.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): TaskDao
}