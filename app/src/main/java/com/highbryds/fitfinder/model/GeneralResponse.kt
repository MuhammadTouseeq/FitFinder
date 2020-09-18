package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @SerializedName("status") var status: Int?,
    @SerializedName("message") var message: String,
    @SerializedName("imageUrl") var imageUrl: String
)