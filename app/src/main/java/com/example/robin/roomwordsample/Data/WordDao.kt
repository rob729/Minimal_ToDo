package com.example.robin.roomwordsample.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WordDao {

    @Insert
    fun insert(word: Word)

    @Delete
    fun deleteTask(word: Word)

    @Query("DELETE FROM word_table")
    fun deleteAll()

    @Query("SELECT * from word_table ORDER BY word ASC")
    fun getAllWords(): LiveData<List<Word>>

    @Query("UPDATE word_table SET is_complete = :mark WHERE word=:task")
    fun toggleCompletion(task: String, mark: Boolean)
}