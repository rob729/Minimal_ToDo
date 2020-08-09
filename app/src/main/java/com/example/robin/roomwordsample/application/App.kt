package com.example.robin.roomwordsample.application

import android.app.Application
import com.example.robin.roomwordsample.utils.StoreSession
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        StoreSession.init(applicationContext)
    }
}