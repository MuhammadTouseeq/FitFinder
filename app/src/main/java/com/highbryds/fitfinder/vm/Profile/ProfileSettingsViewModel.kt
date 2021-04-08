package com.highbryds.fitfinder.vm.Profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.UserProfileVisibility
import com.highbryds.fitfinder.model.carpool.Feedback
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch

class ProfileSettingsViewModel @ViewModelInject constructor(private val provideApiInterface: ApiInterface) :
    ViewModel() {


    fun uploadData(usersData: Feedback, apiResponseCallBack: ApiResponseCallBack) {
        viewModelScope.launch {
            submitFeedback(usersData, apiResponseCallBack)
        }
    }


    fun uploadProfilePref(
        userProfileVisibility: UserProfileVisibility,
        apiResponseCallBack: ApiResponseCallBack
    ) {
        viewModelScope.launch {
            profileVisibility(userProfileVisibility, apiResponseCallBack)
        }
    }

    private suspend fun submitFeedback(
        usersData: Feedback,
        apiResponseCallBack: ApiResponseCallBack
    ) {
        try {
            var response = provideApiInterface.userFeedback(usersData)
            if (response.isSuccessful) {
                apiResponseCallBack.getSuccess("Feedback submitted successfully")
            } else {
                apiResponseCallBack.getError(response.toString())
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError(e.toString())
        }
    }

    private suspend fun profileVisibility(
        userProfileVisibility: UserProfileVisibility,
        apiResponseCallBack: ApiResponseCallBack
    ) {
        try {
            val response = provideApiInterface.userProfileVisibility(userProfileVisibility)
            if (response.isSuccessful) {
                apiResponseCallBack.getSuccess("1")
            } else {
                apiResponseCallBack.getError(response.toString())
            }
        } catch (e: Exception) {
            apiResponseCallBack.getError(e.toString())
        }
    }


}