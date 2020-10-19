package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName

data class CarData (
    @SerializedName("Id") val id : Int,
    @SerializedName("Name") val name : String,
    @SerializedName("car_data") val carData : CarMakeModel
)
{
    override fun toString(): String {
        return name;
    }
}