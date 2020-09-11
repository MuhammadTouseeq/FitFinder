package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName
import javax.inject.Inject

// data and pojo for api response
data class UsersData(
    @SerializedName("name") var name: String,
    @SerializedName("deviceToken") var deviceToken: String?,
    @SerializedName("SocialId")var SocialId: String,
    @SerializedName("SocialType")var SocialType: String,
    @SerializedName("emailAdd")var emailAdd: String,
    @SerializedName("imageUrl")var imageUrl: String,
    @SerializedName("age") var age: Int,
    @SerializedName("Gender ")var Gender : String?,
    @SerializedName("City ")var City : String,
    @SerializedName("Country ")var Country : String){

//    // for update profile constructor without imageURL
//    constructor(name: String,
//                deviceToken: String?,
//                SocialId: String,
//                SocialType: String,
//                emailAdd: String,
//                age: Int,
//                Gender : String?,
//                City : String,
//                Country : String) : this(name , deviceToken,SocialId ,SocialType ,emailAdd , ,age,Gender,City,Country)
}