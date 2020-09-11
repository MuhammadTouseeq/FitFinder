package com.android.indigo.model

import com.google.gson.annotations.SerializedName

// data and pojo for api response
data class UsersData(
    @SerializedName("name") var name: String,
    @SerializedName("deviceToken") var deviceToken: String,
    @SerializedName("SocialId")var SocialId: String,
    @SerializedName("SocialType")var SocialType: String,
    @SerializedName("emailAdd")var emailAdd: String,
    @SerializedName("imageUrl")var imageUrl: String)