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
import com.log4k.v
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject


class HomeMapViewModel @Inject constructor(private val apiInterface: ApiInterface) : ViewModel() {

    lateinit var apiErrorsCallBack: ApiResponseCallBack
    val storiesData = MutableLiveData<List<NearbyStory>>()
    val trendingStoriesData = MutableLiveData<List<NearbyStory>>()
    val categoriesData = MutableLiveData<List<String>>()
    val userLocation = MutableLiveData<LatLng>()



    fun uploadStoryData(userStory: UserStory) {
        viewModelScope.launch {
            postStoryData(userStory)
        }
    }

    fun fetchNearByStoriesData(lat: String, lng: String) {
        viewModelScope.launch {
            getNearByStories(lat, lng)
        }
    }

    fun observeAllNearByStories(): LiveData<List<NearbyStory>> {
        return storiesData;
    }



    suspend fun getNearByStories(lat: String, lng: String) {

        viewModelScope.launch {
            val allNearByStories = apiInterface.getAllNearByStories(lat, lng)

            if (allNearByStories.code() == 200 && allNearByStories.body()?.status.equals("1")) {
                storiesData.value = allNearByStories.body()?.data
                categoriesData.value = allNearByStories.body()?.categories
                apiErrorsCallBack.getSuccess(allNearByStories.message())
            } else {
                apiErrorsCallBack.getError(allNearByStories.message())
            }
            val trendingStories = apiInterface.getTrendingStories()

            if (trendingStories.code() == 200 && trendingStories.body()?.status.equals("1")) {
                trendingStoriesData.value=trendingStories.body()?.data
            }

        }

    }

    suspend fun postStoryData(userStory: UserStory) = try {
        with(userStory)
        {
//                val uploadStory = apiInterface.uploadStory(
//                    toMultipartBody(storyMediaPath),
//                    toRequestBody(storyName),
//                    toRequestBody(SocialId),
//                    toRequestBody(latitude),
//                    toRequestBody(longitude)
//                )

                val uploadStory = apiInterface.uploadStory(
                    storyName,
                    SocialId,
                    latitude,
                    longitude,
                    mediaUrl,
                    chipText,
                    address
                )

            if (uploadStory.code() == 200) {

                val arruser = ArrayList<UsersData>()
                arruser.add(KotlinHelper.getUsersData())
                val nearbyStory: NearbyStory = uploadStory.body()!!.data
                nearbyStory.userData = arruser
                storiesData.value = listOf(nearbyStory)
                apiErrorsCallBack.getSuccess("Successfully uploaded story")
            } else {
                apiErrorsCallBack.getError(uploadStory.message())
            }

        }
    } catch (e: Exception) {
        apiErrorsCallBack.getError(e.toString())
    }

    private fun toMultipartBody(path: String): MultipartBody.Part {
        val file = File(path)
        val reqFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("story", file.name, reqFile)
    }

    private fun toRequestBody(key: String): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), key)
    }

}