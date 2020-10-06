package com.highbryds.fitfinder.ui.OnBoarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.highbryds.fitfinder.R
import kotlinx.android.synthetic.main.activity_second_screen.*


class SecondScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_screen)

        BTN_next.setOnClickListener {
            val intent = Intent(this, ThirdScreen::class.java)
            startActivity(intent)
        }
    }
}