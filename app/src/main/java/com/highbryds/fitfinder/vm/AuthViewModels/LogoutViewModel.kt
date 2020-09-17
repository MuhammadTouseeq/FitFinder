package com.highbryds.fitfinder.vm.AuthViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogoutViewModel @Inject constructor(private val provideApiInterface: ApiInterface): ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack

    fun logoutUser(socialID : String){
        viewModelScope.launch {
            logoutCall(socialID)
        }
    }

    private suspend fun logoutCall(socialID: String){
        try {
            val response = provideApiInterface.logoutUser(socialID)
            if (response.isSuccessful){
                if (response.body()?.status == 1){
                    apiResponseCallBack.getSuccess("User Logout Successfully")
                }
            }else{
                apiResponseCallBack.getError(response.errorBody().toString())
            }
        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }
    }
}