package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class bestmatch(
    @SerializedName("carmake") val carmake : String,
    @SerializedName("carmodel") val carmodel : String,
    @SerializedName("type") val type : String,
    @SerializedName("cellNumber") val cellNumber : String,
    @SerializedName("color") val color : String,
    @SerializedName("AC") val aC : String,
    @SerializedName("regno") val regno : String,
    @SerializedName("vehicleType") val vehicleType : String,
    @SerializedName("totalseats") val totalseats : Int,
    @SerializedName("seatsleft") val seatsleft : Int,
    @SerializedName("preferredcost_min") val preferredcost_min : Int,
    @SerializedName("preferredcost_max") val preferredcost_max : Int,
    @SerializedName("startingtime") val startingtime : String,
    @SerializedName("destination_landmarks") val destination_landmarks : List<FR_SearchCarDestinationLandmarks>,
    @SerializedName("destination") val destination : FR_SearchCarDestinationPoint,
    @SerializedName("starting_point") val starting_point : FR_SearchCarStartingPoint,
    @SerializedName("SocialId") val socialId : String
)