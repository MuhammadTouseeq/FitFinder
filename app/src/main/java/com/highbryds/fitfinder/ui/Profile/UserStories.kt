package com.highbryds.fitfinder.ui.Profile

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.UserStoriesAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.vm.Profile.UserStoriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_user_stories.*
import javax.inject.Inject

@AndroidEntryPoint
class UserStories : AppCompatActivity(), ApiResponseCallBack {

    @Inject
    lateinit var userStoriesViewModel: UserStoriesViewModel
    var userStoriesModel: ArrayList<UserStoriesModel> = ArrayList()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_stories)

        userStoriesViewModel.apiResponseCallBack = this
        RV_Stories.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        userStoriesViewModel.getUserStories(KotlinHelper.getUsersData().SocialId)
        userStoriesViewModel.storiesModel?.observe(this@UserStories, Observer {
            userStoriesModel.addAll(it)
            val adapter = UserStoriesAdapter(userStoriesModel,this)
            RV_Stories.adapter = adapter
        });



    }

    override fun getError(error: String) {
        this.toast(this, error)
    }

    override fun getSuccess(success: String) {
        this.toast(this, success)
    }
}