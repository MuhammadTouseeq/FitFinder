package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class FR_SearchCarStartingPoint (

    @SerializedName("Name") val name : String,
    @SerializedName("latitude") val latitude : Double,
    @SerializedName("longitude") val longitude : Double
)