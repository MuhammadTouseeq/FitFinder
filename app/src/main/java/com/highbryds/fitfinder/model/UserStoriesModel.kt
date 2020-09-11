package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class UserStoriesModel(@SerializedName("mediaUrl")var img: String,
                            @SerializedName("storyName")var name: String,
                            @SerializedName("SocialId")var socialID: String,
                            @SerializedName("latitude")var lat: Int,
                            @SerializedName("longitude")var lng: Int,
                            @SerializedName("createdAt")var date: String)