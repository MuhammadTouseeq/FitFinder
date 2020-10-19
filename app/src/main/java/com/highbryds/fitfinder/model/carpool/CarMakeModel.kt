package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName

data class CarMakeModel (

    @SerializedName("_id") val _id : String,
    @SerializedName("Id") val id : Int,
    @SerializedName("car_details") val car_details : List<CarDetails>)
