package com.highbryds.fitfinder.ui.Profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.highbryds.fitfinder.R

class UserProfileSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_setting)
        init()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainroot, ProfileSettingsFragment())
            .commit()

    }

    fun init() {

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = "Settings"
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }
}