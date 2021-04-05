package com.highbryds.fitfinder.model.carpool

import com.google.gson.annotations.SerializedName
import com.highbryds.fitfinder.model.FR_SearchCarDestinationLandmarks
import com.highbryds.fitfinder.model.FR_SearchCarDestinationPoint
import com.highbryds.fitfinder.model.FR_SearchCarStartingPoint

data class Feedback( @SerializedName("SocialId") val SocialId : String,
                     @SerializedName("cellNumber") val cellNumber : String,
                     @SerializedName("feedbackmsg") val feedbackmsg : String)

