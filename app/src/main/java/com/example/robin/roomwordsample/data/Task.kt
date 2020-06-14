package com.example.robin.roomwordsample.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
data class Task(
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "is_complete") val isComplete: Boolean,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "color") val color: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
