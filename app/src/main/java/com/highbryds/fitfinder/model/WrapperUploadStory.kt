package com.highbryds.fitfinder.model

import com.highbryds.fitfinder.model.TrendingStory
import com.google.gson.annotations.SerializedName

data class WrapperUploadStory (

    @SerializedName("data") var data : NearbyStory,
    @SerializedName("message") val message : String
)