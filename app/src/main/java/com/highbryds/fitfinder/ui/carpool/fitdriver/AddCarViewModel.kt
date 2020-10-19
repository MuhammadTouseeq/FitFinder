package com.highbryds.fitfinder.ui.carpool.fitdriver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.model.carpool.CarData
import com.highbryds.fitfinder.model.carpool.CarMakeModel
import com.highbryds.fitfinder.retrofit.ApiInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCarViewModel @Inject constructor(val apiInterface: ApiInterface) :ViewModel() {
    val arrCarMakeModel = MutableLiveData<List<CarData>>()

    fun getCarMakeModels()
    {
        viewModelScope.launch {

            getAllCars()
        }
    }

    suspend fun getAllCars()
    {
        val response = apiInterface.getCarMakeModels()

        if (response.code()==200)
        {
            arrCarMakeModel.value=response.body()
        }
        else{
          arrCarMakeModel.value=null
        }


    }


}