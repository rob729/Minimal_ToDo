package com.example.robin.roomwordsample.Data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class WordRepository(private val wordDao: WordDao) {
    val allWords: LiveData<List<Word>> = wordDao.getAllWords()

    @WorkerThread
    fun insert(word: Word) {
        wordDao.insert(word)
    }

    @WorkerThread
    fun update(id: Int, name: String, description: String) {
        wordDao.update(id, name, description)
    }

    @WorkerThread
    fun toggleCompletion(id: Int, mark: Boolean) {
        wordDao.toggleCompletion(id, mark)
    }

    @WorkerThread
    fun delete(word: Word) {
        wordDao.deleteTask(word)
    }

}