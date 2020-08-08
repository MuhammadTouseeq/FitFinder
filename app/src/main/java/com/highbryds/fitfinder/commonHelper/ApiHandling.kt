package com.highbryds.fitfinder.commonHelper

import org.json.JSONObject

// To handle messages and error thrown by apis
fun getErrors(error: String):  String{
       return JSONObject(error).get("message").toString()
    }
