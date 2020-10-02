package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName
import javax.inject.Inject

// data and pojo for api response
data class UsersData(
    @SerializedName("headline") var headline: String?,
    @SerializedName("about") var About: String?,
    @SerializedName("name") var name: String,
    @SerializedName("deviceToken") var deviceToken: String?,
    @SerializedName("SocialId")var SocialId: String,
    @SerializedName("SocialType")var SocialType: String,
    @SerializedName("emailAdd")var emailAdd: String,
    @SerializedName("cellNumber")var cellNumber: String,
    @SerializedName("imageUrl")var imageUrl: String,
    @SerializedName("age") var age: Int?,
    @SerializedName("Gender")var Gender : String?,
    @SerializedName("City")var City : String?,
    @SerializedName("Country") var Country : String?){

//    // for update profile constructor
    constructor(name: String,
                deviceToken: String?,
                SocialId: String,
                SocialType: String,
                emailAdd: String,
                cellNumber: String,
                imageUrl: String) : this(null , null ,name , deviceToken,SocialId ,SocialType ,emailAdd ,cellNumber, imageUrl,null,null,null,null)
}