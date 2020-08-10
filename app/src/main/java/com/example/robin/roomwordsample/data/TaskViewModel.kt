package com.example.robin.roomwordsample.data

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robin.roomwordsample.adapter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TaskViewModel @ViewModelInject constructor(private val repository: TaskRepository) :
    ViewModel() {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    var taskList: LiveData<List<Task>> = repository.getAllWords()

    fun insert(task: Task) = scope.launch(Dispatchers.IO) {
        repository.insert(task)
    }


    private fun delete(task: Task) = scope.launch(Dispatchers.IO) {
        repository.delete(task)
    }

    private fun markStatus(id: Int, mark: Boolean) = scope.launch(Dispatchers.IO) {
        repository.toggleCompletion(id, mark)
    }

    private fun update(id: Int, word: String, description: String) = scope.launch(Dispatchers.IO) {
        repository.update(id, word, description)
    }

    fun handleTaskAction(action: Action) {
        when (action) {
            is RemoveTask -> {
                delete(action.task)
            }
            is RestoreTask -> {
                insert(action.task)
            }
            is ToggleTaskStatus -> {
                markStatus(action.id, action.mark)
            }
            is UpdateTask -> {
                update(action.id, action.word, action.description)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}