package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class NearbyStory (

    @SerializedName("isActive") val isActive : Int,
    @SerializedName("mediaUrl") val mediaUrl : String,
    @SerializedName("storyName") val storyName : String,
    @SerializedName("_id") val _id : String,
    @SerializedName("SocialId") val socialId : Int,
    @SerializedName("latitude") val latitude : Double,
    @SerializedName("longitude") val longitude : Double,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("__v") val __v : Int
)