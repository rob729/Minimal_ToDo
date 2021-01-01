package com.example.robin.roomwordsample.data

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllWords() = taskDao.getAllWords()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(id: Int, name: String, description: String) {
        taskDao.update(id, name, description)
    }

    suspend fun toggleCompletion(id: Int, mark: Boolean) {
        taskDao.toggleCompletion(id, mark)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

}