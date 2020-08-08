package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

// data and pojo for api response
data class UsersData(@SerializedName("login") var login: String, @SerializedName("id") var id: Int, @SerializedName("message")var message: String)