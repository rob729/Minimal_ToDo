package com.example.robin.roomwordsample.Data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE word_table "
                + " ADD COLUMN is_complete INTEGER DEFAULT 0")
    }
}