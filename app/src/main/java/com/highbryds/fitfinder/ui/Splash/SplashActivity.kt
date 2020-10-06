package com.highbryds.fitfinder.ui.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.ui.Auth.LoginActivity
import com.highbryds.fitfinder.ui.Main.HomeMapActivity
import com.highbryds.fitfinder.ui.OnBoarding.FirstScreen
import com.highbryds.fitfinder.ui.Profile.UserProfileMain

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val bundle = intent.extras
        if(bundle != null){
            val key = bundle.getString("type")
            PrefsHelper.putString(Constants.Pref_ToOpenStoryAuto, key)
            Log.d("bundle"  , key!!)
            Log.d("bundle"  , PrefsHelper.getString(Constants.Pref_ToOpenStoryAuto))
        }

        if (PrefsHelper.getBoolean(Constants.Pref_IsLogin)){
            val intent = Intent(this , HomeMapActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this , FirstScreen::class.java)
            startActivity(intent)
        }
    }
}