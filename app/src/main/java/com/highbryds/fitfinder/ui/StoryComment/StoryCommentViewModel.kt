package com.highbryds.fitfinder.ui.StoryComment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.log4k.v
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoryCommentViewModel  @Inject constructor(private val apiInterface: ApiInterface): ViewModel(){
    lateinit var apiErrorsCallBack: ApiResponseCallBack

    val commentsData = MutableLiveData<List<StoryComment>>()
    val storyCommentsData = MutableLiveData<List<StoryComment>>()
    fun addStoryComment(socialId:String,storyId:String,comment:String)
    {
        viewModelScope.launch {
            insertStoryComment(socialId,storyId,comment)
        }
    }

    fun fetchStoryComments(storyId:String)
    {
        viewModelScope.launch {
            getStoryComments(storyId)
        }
    }


    suspend fun getStoryComments(storyId:String)
    {
        val response = apiInterface.getStorycomments(storyId)

        if (response.code()==200)
        {
            storyCommentsData.value=response.body()
        }
        else{
            storyCommentsData.value=null
        }


    }

    suspend fun insertStoryComment(socialId:String,storyId:String,comment:String)
    {
        val response = apiInterface.insertStoryComment(socialId,storyId,comment)

        if (response.code()==200&&response.body()?.status.equals("1"))
        {
           commentsData.value=response.body()?.data
        }
        else{
            commentsData.value=null
        }


    }

}