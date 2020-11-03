package com.highbryds.fitfinder.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @SerializedName("status") var status: Int?,
    @SerializedName("message") var message: String,
    @SerializedName("imageUrl") var imageUrl: String,
    @SerializedName("messageTo") var chatting: List<ChattingMsgTo>
)