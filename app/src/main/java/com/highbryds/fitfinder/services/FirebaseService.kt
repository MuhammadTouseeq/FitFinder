package com.highbryds.fitfinder.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.PrefsHelper

class FirebaseService: FirebaseMessagingService() {

    @Override
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("NEW_TOKEN", p0);
        PrefsHelper.putString(Constants.Pref_DeviceToken , p0)
    }
}