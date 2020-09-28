package com.highbryds.fitfinder.ui.OnBoarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import kotlinx.android.synthetic.main.activity_first_screen.*


class FirstScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        next.setOnClickListener {
            val intent = Intent(this, SecondScreen::class.java)
            startActivity(intent)
        }





    }
}