package com.highbryds.fitfinder.model

import com.highbryds.fitfinder.model.TrendingStory
import com.google.gson.annotations.SerializedName

data class WrapperStory (

    @SerializedName("data") val data : List<NearbyStory>,
    @SerializedName("categories") val categories : List<String>,
    @SerializedName("helpcategories") val helpcategories : List<String>,
    @SerializedName("status") val status : String,
    @SerializedName("message") val message : String
)