package com.highbryds.fitfinder.ui.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.ui.Main.HomeMapActivity
import com.highbryds.fitfinder.ui.OnBoarding.FirstScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
            }


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