package com.highbryds.fitfinder.vm.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import javax.inject.Inject

class UpdateProfileViewModel @Inject constructor(private val provideApiInterface: ApiInterface) : ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack

    fun uploadProfile(multipartBody: MultipartBody.Part, requestBody: RequestBody, usersData: UsersData) {
        viewModelScope.launch {
            uploadPic(multipartBody, requestBody , usersData)
        }
    }

    fun uploadUsersData(usersData: UsersData){
        viewModelScope.launch {
            updateProfileData(usersData)
        }
    }

    private suspend fun uploadPic(multipartBody: MultipartBody.Part, requestBody: RequestBody, usersData: UsersData) {
        try {
            var response = provideApiInterface.uploadProfilePic(multipartBody, requestBody)
            if (response.isSuccessful) {
                usersData.imageUrl = response.body()!!.imageUrl
                updateProfileData(usersData)
            } else {
                apiResponseCallBack.getError(response.toString())
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError(e.toString())
        }
    }

    private suspend fun updateProfileData(usersData: UsersData) {
        try {
            var response = provideApiInterface.updateUsers(usersData)
            if (response.isSuccessful) {
                KotlinHelper.updateUserInfo(usersData)
                apiResponseCallBack.getSuccess("Profile Update Successfully")
            } else {
                apiResponseCallBack.getError(response.toString())
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError(e.toString())
        }
    }
}

