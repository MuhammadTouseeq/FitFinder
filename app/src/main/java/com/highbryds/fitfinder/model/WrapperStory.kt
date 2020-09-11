package com.highbryds.fitfinder.model

import com.highbryds.fitfinder.model.TrendingStory
import com.google.gson.annotations.SerializedName

data class WrapperStory (

    @SerializedName("data") val data : List<NearbyStory>,
    @SerializedName("status") val status : Int
)