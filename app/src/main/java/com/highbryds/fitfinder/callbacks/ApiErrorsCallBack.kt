package com.highbryds.fitfinder.callbacks

import javax.inject.Inject


interface ApiErrorsCallBack{
    abstract fun getError(error: String)
}