package com.highbryds.fitfinder.vm.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserStoriesViewModel @Inject constructor(private val provideApiInterface: ApiInterface) : ViewModel() {

    val storiesModel: LiveData<List<UserStoriesModel>>? = MutableLiveData()
    lateinit var apiResponseCallBack: ApiResponseCallBack

    fun getUserStories(userStories: String){
        viewModelScope.launch{
            storiesModel as MutableLiveData
            userStories(userStories)?.let {
                storiesModel.value = it
            }
        }
    }


    private suspend fun userStories(userStories: String) : List<UserStoriesModel>?{
        try {
            val response = provideApiInterface.userStories(userStories)
            return if (response.isSuccessful && !response.body().isNullOrEmpty()){
                response.body()
            }else{
                apiResponseCallBack.getError("No Data Found")
                null
            }
        }catch (e: Exception){
            apiResponseCallBack.getError("No Data Found")
            return null
        }

    }
}