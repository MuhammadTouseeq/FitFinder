package com.highbryds.fitfinder.vm.AuthViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.getErrors
import com.highbryds.fitfinder.model.UserAgent
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val provideApiInterface: ApiInterface) : ViewModel() {

    val userdata: LiveData<UsersData>? = MutableLiveData()
    lateinit var apiResponseCallBack: ApiResponseCallBack
    var isProceed: Boolean = false

     fun loginUser(usersData: UsersData, userAgent: UserAgent){
        viewModelScope.launch {
           userdata as MutableLiveData
            userAgent(userAgent)
            if (isProceed){
                createUser(usersData)?.let {
                    userdata.value = it
                }
            }
        }
    }

    private suspend fun userAgent(userAgent: UserAgent){
        try {
            val response = provideApiInterface.userAgent(userAgent)
            if (response.isSuccessful){
                if (response.body()?.message!!.contains("successfully" , ignoreCase = true)){
                    isProceed = true
                }else{
                    apiResponseCallBack.getError(response.body()?.message.toString())
                }
            }else{
                apiResponseCallBack.getError(getErrors(response.errorBody().toString()))
            }
        }catch (e:Exception){
            apiResponseCallBack.getError(e.toString())
        }

    }

    private suspend fun createUser(usersData: UsersData) : UsersData?{
        try {
            Log.d("userData" ,usersData.toString())
            val response = provideApiInterface.createUsers(usersData)
            return if (response.errorBody() != null) {
                Log.d("ERRORVIEWMODEL", getErrors(response.errorBody().toString()))
                apiResponseCallBack.getError(getErrors(response.errorBody().toString()))
                null
            } else {
                response.body()
            }
        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
            return null
        }
    }
}