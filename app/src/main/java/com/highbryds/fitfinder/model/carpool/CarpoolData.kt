package com.highbryds.fitfinder.model.carpool

data class CarpoolData(
    var status : Int,
    var carpooldata : ArrayList<RideRequest>
) {
}