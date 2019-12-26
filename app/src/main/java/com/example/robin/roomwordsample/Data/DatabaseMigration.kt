package com.example.robin.roomwordsample.Data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE word_table "
                    + " ADD COLUMN is_complete INTEGER DEFAULT 0"
        )
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE word_table ADD COLUMN description TEXT DEFAULT NULL")
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("CREATE TABLE word_table_copy(id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, time TEXT, tag TEXT, description TEXT DEFAULT NULL, is_complete INTEGER DEFAULT 0)")
            database.execSQL("INSERT INTO word_table_copy(word, time, tag, description, is_complete) SELECT word, time, tag, description, is_complete FROM word_table")
            database.execSQL("DROP TABLE word_table")
            database.execSQL("ALTER TABLE word_table_copy RENAME TO word_table")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            database.endTransaction()
        }
    }
}
