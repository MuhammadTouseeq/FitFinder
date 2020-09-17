package com.highbryds.fitfinder.ui.Main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.getErrors
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.UserStory
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class HomeMapViewModel @Inject constructor(private val apiInterface: ApiInterface):ViewModel(){

    lateinit var apiErrorsCallBack: ApiResponseCallBack
    val storiesData = MutableLiveData<List<NearbyStory>>()
    val categoriesData = MutableLiveData<List<String>>()
    val userLocation = MutableLiveData<LatLng>()


    fun uploadStoryData(userStory: UserStory)
    {
        viewModelScope.launch {
            postStoryData(userStory)
        }
    }

    fun fetchNearByStoriesData(lat:String,lng:String)
    {
        viewModelScope.launch {
           getNearByStories(lat,lng)
        }
    }
    fun observeAllNearByStories(): LiveData<List<NearbyStory>>
    {
return storiesData;
    }

    suspend fun getNearByStories(lat:String,lng:String) {

        viewModelScope.launch {
            val allNearByStories = apiInterface.getAllNearByStories(lat, lng)
            if (allNearByStories.code() == 200&&allNearByStories.body()?.status.equals("1")) {
               storiesData.value = allNearByStories.body()?.data
               categoriesData.value = allNearByStories.body()?.categories

                apiErrorsCallBack.getError(allNearByStories.message())
            } else
            {
                apiErrorsCallBack.getSuccess(allNearByStories.message())
            }
        }

    }

     suspend fun postStoryData(userStory: UserStory)
    {

            with(userStory)
            {
                val uploadStory = apiInterface.uploadStory(
                    toMultipartBody(storyMediaPath),
                    toRequestBody(storyName),
                    toRequestBody(SocialId),
                    toRequestBody(latitude),
                    toRequestBody(longitude)
                )
                if (uploadStory.code()==200)
                {
                    apiErrorsCallBack.getError("Successfully uploaded story")
                }
                else{
                    apiErrorsCallBack.getError(uploadStory.message())
                }

        }


    }

    private fun toMultipartBody(path:String):MultipartBody.Part
    {
        val file = File(path)
        val reqFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
  return MultipartBody.Part.createFormData("story", file.name, reqFile)
    }

    private fun toRequestBody(key:String):RequestBody
    {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), key)
    }

}