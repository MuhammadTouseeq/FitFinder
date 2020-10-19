package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class SearchCarApiResponse(
    @SerializedName("bestmatch") val bestmatch: List<FR_SearchCar>,
    @SerializedName("others") val others: List<FR_SearchCar>)