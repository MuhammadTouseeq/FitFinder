package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class NearbyStory (

    @SerializedName("isActive") var isActive : Int,
    @SerializedName("mediaUrl") var mediaUrl : String,
    @SerializedName("storyName") var storyName : String,
    @SerializedName("_id") var _id : String,
    @SerializedName("SocialId") var socialId : String,
    @SerializedName("latitude") var latitude : Double,
    @SerializedName("longitude") var longitude : Double,
    @SerializedName("createdAt") var createdAt : String,
    @SerializedName("__v") var __v : Int,
    @SerializedName("Category") var Category : String?,
    @SerializedName("users_data") var userData : List<UsersData>?,
    @SerializedName("clap_data") var storyClapData : List<StoryClap>?,
    @SerializedName("views_data") var storyViewsData : List<StoryViews>?

)