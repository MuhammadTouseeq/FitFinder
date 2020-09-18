package com.highbryds.fitfinder.vm.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.commonHelper.FTPHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserStoriesViewModelFTP @Inject constructor(): ViewModel() {

    fun sendOverOtp(filePath: String){
        viewModelScope.launch {Dispatchers.IO
           // FTPHelper.ftp(filePath)
        }
    }
}