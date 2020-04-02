package com.example.robin.roomwordsample.Data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {
    val allWords: LiveData<List<Task>> = taskDao.getAllWords()

    @WorkerThread
    fun insert(task: Task) {
        taskDao.insert(task)
    }

    @WorkerThread
    fun update(id: Int, name: String, description: String) {
        taskDao.update(id, name, description)
    }

    @WorkerThread
    fun toggleCompletion(id: Int, mark: Boolean) {
        taskDao.toggleCompletion(id, mark)
    }

    @WorkerThread
    fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

}