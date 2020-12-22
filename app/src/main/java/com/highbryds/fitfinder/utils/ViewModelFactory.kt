package com.highbryds.fitfinder.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.highbryds.fitfinder.retrofit.ApiInterface
import com.highbryds.fitfinder.vm.Profile.UserStoriesViewModel


class ViewModelFactory constructor(private val baseApiInterface: ApiInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(UserStoriesViewModel::class.java!!)) {
            UserStoriesViewModel(this.baseApiInterface) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}