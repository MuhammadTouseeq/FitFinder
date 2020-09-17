package com.highbryds.fitfinder.utils

import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object
    {

        fun getDateTimeFromServer(date:String,format:String): String {
            val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date: Date = fmt.parse(date)

            val fmtOut = SimpleDateFormat(format)
            return fmtOut.format(date)
        }

    }
}