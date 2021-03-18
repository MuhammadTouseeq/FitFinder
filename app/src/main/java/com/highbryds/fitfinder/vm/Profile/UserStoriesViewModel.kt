package com.highbryds.fitfinder.vm.Profile

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.UserProfile
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class UserStoriesViewModel @Inject constructor(private val provideApiInterface: ApiInterface) : ViewModel() {

    init {
        Log.d("###################" , "init")
        viewModelScope.launch {
            //getProfileData(KotlinHelper.getUsersData().SocialId)
            getProfileData(Constants.SocialIdToView!!)
        }
    }


    val storiesModel: LiveData<List<NearbyStory>>? = MutableLiveData()
    val userProfile = MutableLiveData<Response<UserProfile>>()

    lateinit var apiResponseCallBack: ApiResponseCallBack

    fun getUserStories(userStories: String) {
        viewModelScope.launch {
            storiesModel as MutableLiveData
            userStories(userStories)?.let {
                storiesModel.value = it
            }
        }
    }

    fun deactivate(id: String) {
        viewModelScope.launch {
            deactivateStory(id)
        }
    }


    fun getUserProfileData(id: String){
        viewModelScope.launch {
            userProfile as MutableLiveData
            getProfileData(id)?.let {
                userProfile.value
            }
        }
    }

    private suspend fun userStories(userStories: String): List<NearbyStory>? {
        try {
            val response = provideApiInterface.userStories(userStories)
            return if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                response.body()
            } else {
                apiResponseCallBack.getError("No Data Found")
                null
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError("No Data Found")
            return null
        }

    }

    private suspend fun deactivateStory(id: String) {
        try {
            val resposne = provideApiInterface.deactivateStory(id)
            if (resposne.isSuccessful) {
                if (resposne.body()?.status == 1) {
                    apiResponseCallBack.getSuccess("Story Deactivate Successfully")
                }
            } else {
                apiResponseCallBack.getError(resposne.errorBody().toString())
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError(e.toString())
        }
    }


    suspend fun getProfileData(id: String) {
        try {
            val response = provideApiInterface.getUserProfile(id)
            if (response.isSuccessful!!){
                userProfile.postValue(response)
            }else{
                apiResponseCallBack.getError(response.errorBody().toString())
                //return null
            }

        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
            //return null
        }
    }

}