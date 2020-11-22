package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName
import com.highbryds.fitfinder.model.FR_SearchCar

data class RideRequest(
    @SerializedName("carpoolstatus_id") val carpoolstatus_id: String?,
    @SerializedName("socialIdFR") val socialIdFR: String?,
    @SerializedName("NameFR") val NameFR: String?,
    @SerializedName("data") val frSearchcar: FR_SearchCar? = null
)