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
    fun deleteStoryComments(commmentId: String,storyId:String)
    {
        viewModelScope.launch {
            deleteComment(commmentId,storyId)
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
       try {
           val response = apiInterface.insertStoryComment(socialId,storyId,comment)

           if (response.code()==200&&response.body()?.status.equals("1"))
           {
               commentsData.value=response.body()?.data
              // apiErrorsCallBack.getSuccess("comment added Successfully")
           }
           else{
               commentsData.value=null
               apiErrorsCallBack.getError("Failed to add comment")
           }
       }
       catch (e:Exception)
       {
           apiErrorsCallBack.getError(e.toString())
       }


    }


    private suspend fun deleteComment(commmentId: String,storyId: String) {
        try {
            val resposne = apiInterface.deletecomment(commmentId,storyId)
            if (resposne.isSuccessful) {
                if (resposne.body()?.status == 1) {
                    apiErrorsCallBack.getSuccess("comment deleted Successfully")
                }
            } else {
                apiErrorsCallBack.getError(resposne.errorBody().toString())
            }
        } catch (e: Exception) {
            apiErrorsCallBack.getError(e.toString())
        }
    }

}