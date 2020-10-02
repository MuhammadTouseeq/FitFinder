package com.highbryds.fitfinder.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.ProfileMainAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.ProfileBioModel
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.vm.Profile.UserStoriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_user_profile_main.*
import kotlinx.android.synthetic.main.activity_user_profile_main.IV_Cover
import kotlinx.android.synthetic.main.activity_user_profile_main.IV_back
import kotlinx.android.synthetic.main.activity_user_profile_main.IV_back2
import kotlinx.android.synthetic.main.activity_user_profile_main.recyclerView
import javax.inject.Inject


@AndroidEntryPoint
class UserProfileMain : AppCompatActivity(), ApiResponseCallBack {

    @Inject
    lateinit var userStoriesViewModel: UserStoriesViewModel

    lateinit var clapCount: String
    lateinit var storiesCount: String
    lateinit var commentCount: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_main)


        recyclerView.layoutManager = LinearLayoutManager(this , RecyclerView.VERTICAL , false)

//        updateProfile.setOnClickListener {
//            val intent = Intent(this, UpdateProfile::class.java)
//            startActivity(intent)
//        }
//
//        stories.setOnClickListener {
//            val intent = Intent(this, com.highbryds.fitfinder.ui.Profile.UserStories::class.java)
//            startActivity(intent)
//        }

        IV_back.setOnClickListener {
            finish()
        }
        IV_back2.setOnClickListener {
            finish()
        }

        userStoriesViewModel.apiResponseCallBack = this
        userStoriesViewModel.getUserProfileData(KotlinHelper.getUsersData().SocialId)
        userStoriesViewModel.userProfile?.observe(this@UserProfileMain, Observer {
            try {
//                UserClaps.text = it.body()?.clap_count.toString()
//                UserComments.text = it.body()?.comments_count.toString()
//                UserStories.text = it.body()?.stories_count.toString()
//                Heading.text = it.body()?.user_details?.get(0)?.Headline
//                About.text = it.body()?.user_details?.get(0)?.About

                clapCount = it.body()?.clap_count.toString()
                storiesCount = it.body()?.stories_count.toString()
                commentCount = it.body()?.comments_count.toString()

                val usersData = UsersData(it.body()?.user_details?.get(0)?.Headline!!, it.body()?.user_details?.get(0)?.About!!,it.body()?.user_details?.get(0)?.name!!,it.body()?.user_details?.get(0)?.deviceToken,
                    it.body()?.user_details?.get(0)?.SocialId!!, it.body()?.user_details?.get(0)?.SocialType!!,
                    it.body()?.user_details?.get(0)?.emailAdd!!, it.body()?.user_details?.get(0)?.cellNumber!!,it.body()?.user_details?.get(0)?.imageUrl!!,
                    if (it.body()?.user_details?.get(0)?.age != null) Integer.parseInt(it.body()?.user_details?.get(0)?.age!!) else 0,it.body()?.user_details?.get(0)?.Gender!!,it.body()?.user_details?.get(0)?.City!!,it.body()?.user_details?.get(0)?.Country!!)
                KotlinHelper.updateUserInfo(usersData)

                setUserData();

            }catch (e: Exception){}

        })

    }

    override fun onResume() {
        super.onResume()
        if (PrefsHelper.getBoolean(Constants.Pref_IsProfileUpdate)){
            userStoriesViewModel.getUserProfileData(KotlinHelper.getUsersData().SocialId)
            PrefsHelper.putBoolean(Constants.Pref_IsProfileUpdate , false)
        }
    }

    private fun setUserData() {

        Glide
            .with(this)
            .load(KotlinHelper.getUsersData().imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(IV_Cover);


        toolbarTitle.text = KotlinHelper.getUsersData().name

        val items = ArrayList<ProfileBioModel>();
        items.add(ProfileBioModel(KotlinHelper.getUsersData().imageUrl , storiesCount , clapCount
            ,commentCount, KotlinHelper.getUsersData().headline!! , KotlinHelper.getUsersData().About!!))
        val adapter = ProfileMainAdapter(items , this)
        recyclerView.adapter = adapter

    }

    override fun getError(error: String) {
        this.toast(this , error.toString())
    }

    override fun getSuccess(success: String) {
        this.toast(this , success.toString())
    }
}