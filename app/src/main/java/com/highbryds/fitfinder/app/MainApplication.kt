package com.highbryds.fitfinder.app

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
@Suppress("DEPRECATION")
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)


        // Initialize the Prefs class

        // Initialize the Prefs class
        PrefsHelper.Builder()
            .setContext(this)
            .setMode(MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}