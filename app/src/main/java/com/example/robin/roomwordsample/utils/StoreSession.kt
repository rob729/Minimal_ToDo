package com.example.robin.roomwordsample.utils

import android.content.Context
import android.content.SharedPreferences

object StoreSession {

    lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        if (!this::sharedPreferences.isInitialized) {
            sharedPreferences =
                context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun write(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun read(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun write(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun readString(key: String): String {
        return sharedPreferences.getString(key, " ").toString()
    }
}