package com.highbryds.fitfinder.callbacks

import retrofit2.Response
import javax.inject.Inject


interface ApiResponseCallBack{
    abstract fun getError(error: String)
    abstract fun getSuccess(success: String)
}