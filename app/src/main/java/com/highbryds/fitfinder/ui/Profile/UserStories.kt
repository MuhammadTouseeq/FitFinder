package com.highbryds.fitfinder.ui.Profile

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.UserStoriesAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.vm.Profile.UserStoriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_user_stories.*
import javax.inject.Inject

@AndroidEntryPoint
class UserStories : AppCompatActivity(), ApiResponseCallBack , StoryCallback{

    @Inject
    lateinit var userStoriesViewModel: UserStoriesViewModel
    var userStoriesModel: ArrayList<NearbyStory> = ArrayList()
    var itemPosition: Int = 0
    lateinit var adapter: UserStoriesAdapter

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_stories)

        userStoriesViewModel.apiResponseCallBack = this
        RV_Stories.layoutManager = GridLayoutManager(this, 2)

        userStoriesViewModel.getUserStories(KotlinHelper.getUsersData().SocialId)
        userStoriesViewModel.storiesModel?.observe(this@UserStories, Observer {
            userStoriesModel.addAll(it)
            loadingProgress.visibility = View.GONE
            adapter = UserStoriesAdapter(userStoriesModel, this , this)
            RV_Stories.adapter = adapter
        });



    }

    override fun getError(error: String) {
        this.toast(this, error)
        loadingProgress.visibility = View.GONE
    }

    override fun getSuccess(success: String) {
        this.toast(this, success)
        if (success.equals("Story Deactivate Successfully" , true)){
            userStoriesModel.removeAt(itemPosition)
            adapter.notifyDataSetChanged()
        }
    }

    override fun storyItemPosition(position: Int) {
        this.toast(this , "delete ${position}")
        itemPosition = position
        userStoriesViewModel.deactivate(userStoriesModel.get(position)._id)
    }
}