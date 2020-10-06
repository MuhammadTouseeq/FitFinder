package com.highbryds.fitfinder.ui.OnBoarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.ui.Splash.SplashActivity
import kotlinx.android.synthetic.main.activity_third_screen.*

class ThirdScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_screen)


        BTN_next.setOnClickListener {
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }

    }
}