package com.highbryds.fitfinder.vm.AuthViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.room.Dao
import com.highbryds.fitfinder.room.tables.Users
import com.highbryds.fitfinder.commonHelper.getErrors
import kotlinx.coroutines.launch
import javax.inject.Inject

// injecting the retrofit singleton until the app is alive - by hilt
class MainViewModel @Inject constructor(private val provideApiInterface: ApiInterface,
                                        private val getDatabaseDAO: Dao): ViewModel() {

    val userdata: LiveData<List<UsersData>>? = MutableLiveData()
    lateinit var apiResponseCallBack: ApiResponseCallBack

    init {
        viewModelScope.launch {
          userdata as MutableLiveData
            getUsersData()?.let {
                userdata.value = it
                var users = Users(0 , "123")
                getDatabaseDAO.insertUser(users)
            }
        }
    }


    // using coroutine and void repository seprate class to avoid maximum classes for single line
    private suspend fun getUsersData(): List<UsersData>? {
        val errBody = provideApiInterface.getUsers().errorBody()?.string()
        return if (errBody != null) {
            Log.d("ERRORVIEWMODEL", getErrors(errBody))
            apiResponseCallBack.getError(getErrors(errBody))
            null
        } else {
            provideApiInterface.getUsers().body()
        }
    }
}