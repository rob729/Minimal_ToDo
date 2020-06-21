package com.example.robin.roomwordsample.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task)

    @Query("UPDATE word_table SET description=:desc, word=:word WHERE id=:id")
    fun update(id: Int, word: String, desc: String)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM word_table")
    fun deleteAll()

    @Query("SELECT * from word_table ORDER BY id ASC")
    fun getAllWords(): LiveData<List<Task>>

    @Query("UPDATE word_table SET is_complete = :mark WHERE id=:id")
    fun toggleCompletion(id: Int, mark: Boolean)
}