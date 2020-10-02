package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class UserDetails(
    @SerializedName("Headline") var Headline : String,
    @SerializedName("About") var About : String,
    @SerializedName("age") var age : String?,
    @SerializedName("Gender") var Gender : String,
    @SerializedName("City") var City : String,
    @SerializedName("Country") var Country : String,
    @SerializedName("imageUrl") var imageUrl : String,
    @SerializedName("name") var name : String,
    @SerializedName("deviceToken") var deviceToken : String,
    @SerializedName("SocialId") var SocialId : String,
    @SerializedName("emailAdd") var emailAdd : String,
    @SerializedName("cellNumber") var cellNumber : String,
    @SerializedName("SocialType") var SocialType : String
)

