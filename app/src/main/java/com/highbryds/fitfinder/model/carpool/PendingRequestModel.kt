package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName
import com.highbryds.fitfinder.model.FR_SearchCar

data class PendingRequestModel(@SerializedName("carpooldata") val carpooldata: List<FR_SearchCar>? ,
@SerializedName("carrequestdata") val carrequestdata: List<FR_SearchCar>?)