package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class Chatting(@SerializedName("message") val msg: String,
                    @SerializedName("msgTo") val msgTo: String,
                    @SerializedName("msgFromName") val msgFromName: String,
                    @SerializedName("msgFromProfile") val msgFromProfile: String,
                    @SerializedName("msgTimeStamp") val msgTimeStamp: String,
                    @SerializedName("msgFromSocialId") val msgFromSocialID: String)