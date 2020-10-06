package com.highbryds.fitfinder.ui.OnBoarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.ProfileMainAdapter
import com.highbryds.fitfinder.adapter.UserStoriesAdapter
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.ProfileBioModel
import kotlinx.android.synthetic.main.activity_first_screen.*
import kotlinx.android.synthetic.main.test_cover_image.*


class FirstScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        BTN_next.setOnClickListener {
            val intent = Intent(this, SecondScreen::class.java)
            startActivity(intent)
        }


    }
}