package com.example.robin.roomwordsample.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TaskViewModel(application: Application) : AndroidViewModel(Application()) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: TaskRepository
    val allWords: LiveData<List<Task>>

    init {
        val wordsDao = TaskRoomDatabase.getDatabase(application, scope).wordDao()
        repository = TaskRepository(wordsDao)
        allWords = repository.allWords
    }

    fun insert(task: Task) = scope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    fun delete(task: Task) = scope.launch(Dispatchers.IO) {
        repository.delete(task)
    }

    fun markAsComplete(id: Int, mark: Boolean) = scope.launch(Dispatchers.IO) {
        repository.toggleCompletion(id, mark)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun update(id: Int, word: String, description: String) = scope.launch(Dispatchers.IO) {
        repository.update(id, word, description)
    }

}