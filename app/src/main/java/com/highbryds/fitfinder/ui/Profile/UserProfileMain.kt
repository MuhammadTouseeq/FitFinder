package com.highbryds.fitfinder.ui.Profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.model.UsersData
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.activity_user_profile_main.*
import kotlinx.android.synthetic.main.activity_user_profile_main.IV_bio
import kotlinx.android.synthetic.main.activity_user_profile_main.updateProfile


class UserProfileMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_main)


        updateProfile.setOnClickListener {
            val intent = Intent(this, UpdateProfile::class.java)
            startActivity(intent)
        }

        stories.setOnClickListener {
            val intent = Intent(this, UserStories::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setUserData();
    }

    private fun setUserData() {

        Glide
            .with(this)
            .load(KotlinHelper.getUsersData().imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(IV_bio);

        Glide
            .with(this)
            .load(KotlinHelper.getUsersData().imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(IV_Cover);

    }
}