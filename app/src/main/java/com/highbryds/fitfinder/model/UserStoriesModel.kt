package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class UserStoriesModel(@SerializedName("mediaUrl")var img: String,
                            @SerializedName("storyName")var name: String,
                            @SerializedName("_id")var _id: String,
                            @SerializedName("SocialId")var socialID: String,
                            @SerializedName("latitude")var lat: Double,
                            @SerializedName("longitude")var lng: Double,
                            @SerializedName("createdAt")var date: String)