package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class NearbyStory (

    @SerializedName("isActive") val isActive : Int,
    @SerializedName("mediaUrl") val mediaUrl : String,
    @SerializedName("storyName") val storyName : String,
    @SerializedName("_id") val _id : String,
    @SerializedName("SocialId") val socialId : String,
    @SerializedName("latitude") val latitude : Double,
    @SerializedName("longitude") val longitude : Double,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("__v") val __v : Int,
    @SerializedName("users_data") val userData : List<UsersData>,
    @SerializedName("clap_data") val storyClapData : List<StoryClap>

)