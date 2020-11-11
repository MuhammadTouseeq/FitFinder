package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName
import com.highbryds.fitfinder.model.FR_SearchCarDestinationLandmarks
import com.highbryds.fitfinder.model.FR_SearchCarDestinationPoint
import com.highbryds.fitfinder.model.FR_SearchCarStartingPoint
import com.highbryds.fitfinder.model.UsersData

data class RideRequest(

    @SerializedName("status") var status : String,
    @SerializedName("_id") var _id : String,
    @SerializedName("carmodel") var carmodel : String,
    @SerializedName("type") var type : String,
    @SerializedName("cellNumber") var cellNumber : String,
    @SerializedName("color") var color : String,
    @SerializedName("AC") var aC : String,
    @SerializedName("regno") var regno : String,
    @SerializedName("vehicleType") var vehicleType : String,
    @SerializedName("totalseats") var totalseats : Int,
    @SerializedName("seatsleft") var seatsleft : Int,
    @SerializedName("preferredcost_min") var preferredcost_min : Int,
    @SerializedName("preferredcost_max") var preferredcost_max : Int,
    @SerializedName("startingtime") var startingtime : String?=null,
    @SerializedName("destination_landmarks") var destination_landmarks : List<FR_SearchCarDestinationLandmarks>?=null,
    @SerializedName("destination") var destination : FR_SearchCarDestinationPoint?=null,
    @SerializedName("starting_point") var starting_point : FR_SearchCarStartingPoint?=null,
    @SerializedName("Fit-rider") var usersData : UsersData?=null,
    @SerializedName("SocialId") var socialId : String?=null
)
enum class RIDE_STATUS{
    pending,cancelled,accepted
}