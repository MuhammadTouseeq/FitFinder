package com.highbryds.fitfinder.ui.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.ui.Auth.LoginActivity
import com.highbryds.fitfinder.ui.Main.HomeMapActivity
import com.highbryds.fitfinder.ui.OnBoarding.FirstScreen
import com.highbryds.fitfinder.ui.Profile.UserProfileMain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({

            // PrefsHelper.putString("FCMFCM" , "")
            if (PrefsHelper.getString("FCMFCM" , "").equals("TRUE")){
                this.toast(this , PrefsHelper.getString("FCMFCM2" , "nhi aya"))
            }

            try {
                val bundle = intent.extras
                if(bundle != null){
                    val key = bundle.getString("type")
                    PrefsHelper.putString(Constants.Pref_ToOpenStoryAuto, key)
                    Log.d("bundle"  , key!!)
                    Log.d("bundle"  , PrefsHelper.getString(Constants.Pref_ToOpenStoryAuto))
                }
            }catch (e: Exception){}


            if (PrefsHelper.getBoolean(Constants.Pref_IsLogin)){
                val intent = Intent(this , HomeMapActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this , FirstScreen::class.java)
                startActivity(intent)
            }
            finish()

        }, 3000)


    }
}