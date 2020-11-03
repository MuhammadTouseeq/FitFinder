package com.highbryds.fitfinder.ui.carpool.fitdriver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.SearchCarApiResponse
import com.highbryds.fitfinder.model.carpool.FD_CarPool
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

class FD_CarpoolViewModel @Inject constructor(private val provideApiInterface: ApiInterface) :
    ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack
    var carPoolList =  MutableLiveData<SearchCarApiResponse>()


    fun submitToCarpool(  model: FD_CarPool){
        viewModelScope.launch {
            addtoCarpool(model)
        }
    }

    private suspend fun addtoCarpool(model: FD_CarPool){
        try {
            val response = provideApiInterface.addToCarPool(model);
            if (response.isSuccessful){
                apiResponseCallBack.getSuccess("Successfully added to carpool")
            }else{
                apiResponseCallBack.getError("Something went wrong")
            }

        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }


    }
}