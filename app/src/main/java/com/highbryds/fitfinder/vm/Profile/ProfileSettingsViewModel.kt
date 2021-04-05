package com.highbryds.fitfinder.vm.Profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
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
}