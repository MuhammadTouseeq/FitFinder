package com.highbryds.fitfinder.callbacks



interface ApiResponseCallBack{
    abstract fun getError(error: String)
    abstract fun getSuccess(success: String)
}