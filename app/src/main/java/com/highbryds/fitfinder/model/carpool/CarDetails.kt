package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName

data class CarDetails (
    @SerializedName("Id") val id : Int,
    @SerializedName("Name") val name : String
)
{
    override fun toString(): String {
        return name
    }
}