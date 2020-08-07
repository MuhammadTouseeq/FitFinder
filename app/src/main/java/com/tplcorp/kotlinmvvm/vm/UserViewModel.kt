package com.tplcorp.kotlinmvvm.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tplcorp.kotlinmvvm.Retrofit.ApiInterface
import com.tplcorp.kotlinmvvm.Retrofit.getRetroClient
import com.tplcorp.kotlinmvvm.model.UsersData
import com.tplcorp.kotlinmvvm.utils.ApiHandling
import kotlinx.coroutines.launch
import javax.inject.Inject

// injecting the retrofit singleton until the app is alive - by hilt
class UserViewModel @Inject constructor(private val provideApiInterface: ApiInterface): ViewModel() {

    val userdata: LiveData<List<UsersData>>? = MutableLiveData()

    init {
        viewModelScope.launch {
            userdata as MutableLiveData
            getUsersData()?.let {
                userdata.value = it
            }
        }

    }


    // using coroutine and void repository seprate class to avoid maximum classes for single line
    private suspend fun getUsersData(): List<UsersData>? {
        val errBody = provideApiInterface.getUsers().errorBody()
        return if (errBody != null) {
            Log.d("ERRORVIEWMODEL", ApiHandling.getErrors(errBody.string()))
            null
        } else {
            provideApiInterface.getUsers().body()
        }
    }
}