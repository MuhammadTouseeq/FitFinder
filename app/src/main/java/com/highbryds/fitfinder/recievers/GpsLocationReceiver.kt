package com.highbryds.snapryde.rider_app.recievers

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.highbryds.fitfinder.commonHelper.AppUtils
import com.highbryds.fitfinder.ui.Main.HomeMapActivity


class GpsLocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action!!.matches("android.location.PROVIDERS_CHANGED".toRegex())) {

            Toast.makeText(context, "GPS enable " + AppUtils.isGpsEnabled(context), Toast.LENGTH_LONG).show()

            if(AppUtils.isGpsEnabled(context)==false)
            {
                val i = Intent(context.applicationContext, HomeMapActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            }

        }
    }


}