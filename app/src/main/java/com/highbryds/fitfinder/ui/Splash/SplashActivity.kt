package com.highbryds.fitfinder.ui.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.ui.Auth.LoginActivity
import com.highbryds.fitfinder.ui.Profile.UserProfileMain

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (PrefsHelper.getBoolean(Constants.Pref_IsLogin)){
            val intent = Intent(this , UserProfileMain::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
        }
    }
}