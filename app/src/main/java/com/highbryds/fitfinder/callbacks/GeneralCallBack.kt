package com.highbryds.fitfinder.callbacks

interface GeneralCallBack {
    fun eventOccur(carpoolstatus_id: String)
    fun eventOccurCancelRideRating(carpoolstatus_id: String , socialID: String)
}