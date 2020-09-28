package com.highbryds.fitfinder.vm.Main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.SingleStoryModel
import com.highbryds.fitfinder.model.dataTemp
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class StoryViewModel @Inject constructor(private val apiInterface: ApiInterface): ViewModel() {


    lateinit var apiErrorsCallBack: ApiResponseCallBack
    val singleStory = MutableLiveData<dataTemp>()


    fun getStoryById(storyId: String) {
        viewModelScope.launch {
            getStory(storyId)
        }
    }

    private suspend fun getStory(storyId: String) {
        try {
            val singleStoryModel = SingleStoryModel(storyId)
            val response = apiInterface.getStoryByID(singleStoryModel)
            if(response.isSuccessful){
                singleStory.value = response.body()
            }else{
                apiErrorsCallBack.getError(response.errorBody().toString())
            }
        }catch (e: Exception){
            apiErrorsCallBack.getError(e.toString())
        }

    }

}


