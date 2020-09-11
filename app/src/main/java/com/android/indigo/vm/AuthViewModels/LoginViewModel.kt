package com.android.indigo.vm.AuthViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.indigo.commonHelper.getErrors
import com.android.indigo.model.UsersData
import com.android.indigo.retrofit.ApiInterface
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val provideApiInterface: ApiInterface) : ViewModel() {

    val userdata: LiveData<UsersData>? = MutableLiveData()
    lateinit var apiErrorsCallBack: ApiResponseCallBack
    lateinit var usersData: UsersData

//    init {
//        viewModelScope.launch {
//            userdata as MutableLiveData
//            createUser()?.let {
//                userdata.value = it
//            }
//        }
//    }

    public fun hitApi(){
        viewModelScope.launch {
            userdata as MutableLiveData
            createUser()?.let {
                userdata.value = it
            }
        }
    }


    private suspend fun createUser() : UsersData?{
        try {
            usersData = UsersData("da" , "123" , "123" , "FB", "dani@gmail.com" , "asd")
            val errBody = provideApiInterface.createUsers(usersData).errorBody()?.string()
            return if (errBody != null) {
                Log.d("ERRORVIEWMODEL", getErrors(errBody))
                apiErrorsCallBack.getError(getErrors(errBody))
                null
            } else {
                provideApiInterface.createUsers(usersData).body()
            }
        }catch (e: Exception){
            apiErrorsCallBack.getError(e.toString())
            return null
        }
    }
}