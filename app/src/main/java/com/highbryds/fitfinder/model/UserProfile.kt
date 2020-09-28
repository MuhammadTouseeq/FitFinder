package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("clap_count") var clap_count : Int,
    @SerializedName("stories_count") var stories_count : Int,
    @SerializedName("comments_count") var comments_count : Int,
    @SerializedName("user_details") var user_details : List<UserDetails>
)