package com.highbryds.fitfinder.ui.StoryView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.getErrors
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.StorySpam
import com.highbryds.fitfinder.model.UserStory
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.log4k.v
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class StoryFullViewModel @Inject constructor(private val apiInterface: ApiInterface) : ViewModel() {

    lateinit var apiErrorsCallBack: ApiResponseCallBack

    val clapsData = MutableLiveData<Int>()

    fun insetStoryClap(socialId: String, storyId: String) {
        viewModelScope.launch {
            insertClap(socialId, storyId)
        }
    }


    fun spamStoryById(spam: StorySpam){
        viewModelScope.launch {
            spamStory(spam)
        }
    }

    suspend fun insertClap(socialId: String, storyId: String) {


        val response = apiInterface.insertStoryClap(
            socialId, storyId
        )
        if (response.code() == 200 && response.body()?.status.equals("1")) {
            clapsData.value = 1
        } else {
            clapsData.value = 0


        }


    }


    suspend fun spamStory(spam: StorySpam){
        val response = apiInterface.submitSpam(spam)
        try {
            if(response.isSuccessful){
                if (response.body()?.status == 1){
                    apiErrorsCallBack.getSuccess("Story Spam Successfully")
                }
            }else{
                apiErrorsCallBack.getError(response.errorBody().toString())
            }
        }catch (e: Exception){
            apiErrorsCallBack.getError(e.toString())
        }

    }

}