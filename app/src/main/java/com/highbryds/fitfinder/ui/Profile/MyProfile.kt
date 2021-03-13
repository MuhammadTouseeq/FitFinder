package com.highbryds.fitfinder.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.highbryds.fitfinder.vm.Profile.UserStoriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_third_screen.*
import kotlinx.android.synthetic.main.activity_user_profile_main.*
import kotlinx.android.synthetic.main.myprofile.*
import javax.inject.Inject


@AndroidEntryPoint
class MyProfile : AppCompatActivity(), ApiResponseCallBack {
    @Inject
    lateinit var userStoriesViewModel: UserStoriesViewModel

    @Inject
    lateinit var provideApiInterface: ApiInterface


    lateinit var clapCount: String
    lateinit var storiesCount: String
    lateinit var commentCount: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myprofile)
        userStoriesViewModel.apiResponseCallBack = this

        userStoriesViewModel.userProfile
            ?.observe(this@MyProfile, Observer {
                it?.let {
                    clapCount = it.body()?.clap_count.toString()
                    storiesCount = it.body()?.stories_count.toString()
                    commentCount = it.body()?.comments_count.toString()

                    val usersData = UsersData(
                        it.body()?.user_details?.get(0)?.Headline!!,
                        it.body()?.user_details?.get(0)?.About!!,
                        it.body()?.user_details?.get(0)?.name!!,
                        it.body()?.user_details?.get(0)?.deviceToken,
                        it.body()?.user_details?.get(0)?.SocialId!!,
                        it.body()?.user_details?.get(0)?.SocialType!!,
                        it.body()?.user_details?.get(0)?.emailAdd!!,
                        it.body()?.user_details?.get(0)?.cellNumber!!,
                        it.body()?.user_details?.get(0)?.imageUrl!!,
                        if (it.body()?.user_details?.get(0)?.age != null) Integer.parseInt(
                            it.body()?.user_details?.get(
                                0
                            )?.age!!
                        ) else 0,
                        it.body()?.user_details?.get(0)?.Gender!!,
                        it.body()?.user_details?.get(0)?.City!!,
                        it.body()?.user_details?.get(0)?.Country!!
                    )
                    KotlinHelper.updateUserInfo(usersData)

                    setUserData();
                }
            })
    }


    private fun setUserData() {

        Glide
            .with(this)
            .load(KotlinHelper.getUsersData().imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(IV_bio);

        name.setText(KotlinHelper.getUsersData().name)
        profession.setText(KotlinHelper.getUsersData().headline)
        stories.setText(storiesCount)
        claps.setText(clapCount)
        comments.setText(commentCount)
        email.setText(KotlinHelper.getUsersData().emailAdd)
        about_heading.setText("About " + KotlinHelper.getUsersData().name)
        about_des.setText(KotlinHelper.getUsersData().About)
        edit.setOnClickListener({

            val intent = Intent(this, UpdateProfile::class.java)
            startActivity(intent)

        })

        back.setOnClickListener({

            finish()

        })

    }

    override fun getError(error: String) {
        this.toast(this, error.toString())
    }

    override fun getSuccess(success: String) {
        Log.d("##app", "Success !")
        this.toast(this, success.toString())
    }

    override fun onResume() {
        super.onResume()
        if (PrefsHelper.getBoolean(Constants.Pref_IsProfileUpdate)) {
            userStoriesViewModel.getUserProfileData(KotlinHelper.getUsersData().SocialId)
            PrefsHelper.putBoolean(Constants.Pref_IsProfileUpdate, false)
        }
    }
}