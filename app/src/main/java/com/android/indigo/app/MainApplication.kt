package com.android.indigo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
@Suppress("DEPRECATION")
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}