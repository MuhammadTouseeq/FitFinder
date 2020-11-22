package com.highbryds.fitfinder.vm.CarPool

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.SearchCarApiResponse
import com.highbryds.fitfinder.model.carpool.PendingRequestModel
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class FR_SearchCarVM @Inject constructor(private val provideApiInterface: ApiInterface) :
    ViewModel() {

    lateinit var apiResponseCallBack: ApiResponseCallBack
    var carPoolList =  MutableLiveData<SearchCarApiResponse>()
    val pendingRequest =  MutableLiveData<PendingRequestModel>()


    fun SearchCar(frFR_SearchCar: FR_SearchCar){
        viewModelScope.launch {
            getCarCall(frFR_SearchCar)
        }
    }

    fun sendRequest(rideRequest: RideRequest){
        viewModelScope.launch {
            sendRequestCall(rideRequest)
        }
    }

    fun getPendingRequest(rideRequest: RideRequest){
        viewModelScope.launch {
            getPendingRequestsCall(rideRequest)
        }
    }

    private suspend fun getCarCall(frFR_SearchCar: FR_SearchCar){
        try {
            val response = provideApiInterface.searchCarPool(frFR_SearchCar);
            if (response.isSuccessful){
                carPoolList?.value = response.body()
            }else{
                apiResponseCallBack.getError("Something went wrong")
            }
        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }

    }

    private suspend fun sendRequestCall(rideRequest: RideRequest){
        try {
            val response = provideApiInterface.sendRiderRequest(rideRequest)
            if (response.isSuccessful){
                apiResponseCallBack.getSuccess(response.body()!!.message)
            }else{
                apiResponseCallBack.getError("Something went wrong")
            }
        }catch (e: Exception){
            apiResponseCallBack.getError(e.toString())
        }
    }

    private suspend fun getPendingRequestsCall(rideRequest: RideRequest){
        return try {
            val response = provideApiInterface.getPendingRequests(rideRequest)
            if (response.isSuccessful){
                pendingRequest.value = response.body()
            }else{
                apiResponseCallBack.getError("Something went wrong")
            }
        }catch (e : Exception){
            apiResponseCallBack.getError(e.toString())
        }
    }
}