package com.highbryds.fitfinder.model

import com.highbryds.fitfinder.model.TrendingStory
import com.google.gson.annotations.SerializedName

data class WrapperStoryComment (

    @SerializedName("data") val data : List<StoryComment>,
    @SerializedName("status") val status : String,
    @SerializedName("message") val message : String
)