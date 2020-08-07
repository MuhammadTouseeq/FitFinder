package com.tplcorp.kotlinmvvm.utils

import org.json.JSONObject

// To handle messages and error thrown by apis
object ApiHandling {

    fun getErrors(error: String):  String{
       return JSONObject(error).get("message").toString()
    }
}