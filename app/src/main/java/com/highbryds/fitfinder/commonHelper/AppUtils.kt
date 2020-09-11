package com.highbryds.fitfinder.commonHelper

import android.content.Context
import android.provider.Settings

object AppUtils {



    public fun isGpsEnabled(context: Context): Boolean {
        val contentResolver = context.contentResolver
        // Find out what the settings say about which providers are enabled
        //  String locationMode = "Settings.Secure.LOCATION_MODE_OFF";
        val mode = Settings.Secure.getInt(
            contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF
        )
        return if (mode != Settings.Secure.LOCATION_MODE_OFF) {
            true
            /* if (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) {
                    locationMode = "Device only. Uses GPS to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) {
                    locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                }*/
        } else {
            false
        }
    }
}