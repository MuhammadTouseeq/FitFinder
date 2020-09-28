package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class dataTemp(@SerializedName("message") val  msg : String,
                    @SerializedName("data") val data: List<NearbyStory>)