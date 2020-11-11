package com.highbryds.fitfinder.ui.carpool.fitdriver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.SearchCarApiResponse
import com.highbryds.fitfinder.model.carpool.FD_CarPool
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.model.carpool.RideStatus
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

class FD_CarpoolViewModel @Inject constructor(private val provideApiInterface: ApiInterface) :
    ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack
    var carPoolList =  MutableLiveData<SearchCarApiResponse>()
    var riderRequestData =  MutableLiveData<ArrayList<RideRequest>>()
    var isRideStatusChange =  MutableLiveData<Boolean>()


    fun submitToCarpool(  model: FD_CarPool){
        viewModelScope.launch {
            addtoCarpool(model)
        }
    }
    fun changeRideStatus(  model: RideStatus){
        viewModelScope.launch {
            postChangeRideStatus(model)
        }
    }

    fun getRiderRequest(  socialID: String){
        viewModelScope.launch {
            getMyRequests(socialID)
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


    private suspend fun postChangeRideStatus(model: RideStatus){
        try {
            val response = provideApiInterface.changeRideStatus(model);
            if (response.isSuccessful) {

                if (response.body()!!.status==1) {
                    isRideStatusChange.postValue(true)
                    apiResponseCallBack.getSuccess(response.body()!!.message)

                } else {
                    isRideStatusChange.postValue(false)

                    apiResponseCallBack.getError(response.body()!!.message)
                }
            }
            else
            {
                apiResponseCallBack.getError("Something went wrong")
            }
        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }


    }

    private suspend fun getMyRequests(socialID:String){
        try {
            val response = provideApiInterface.getMyRideRequest(socialID);
            if (response.isSuccessful) {

                if (response.body()!!.status == 1) {

                    response.body()?.let {
                        riderRequestData.value = response.body()?.carpooldata
                    }
                } else {
                    apiResponseCallBack.getError("Data is not found")

                }
            }
                else{
                apiResponseCallBack.getError("Something went wrong")
            }


        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }


    }
}