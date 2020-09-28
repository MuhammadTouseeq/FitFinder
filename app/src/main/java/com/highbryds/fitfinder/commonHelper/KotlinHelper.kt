package com.highbryds.fitfinder.commonHelper

import android.os.Build
import com.google.gson.Gson
import com.highbryds.fitfinder.model.UsersData
import org.json.JSONObject
import java.io.File
import java.util.*

class KotlinHelper {
    companion object{
        fun isJsonObjNull(obj: JSONObject, value: String) : String{
            return try {
                if (obj.getString(value) != null){
                    obj.getString(value).toString()
                }else{
                    ""
                }
            }catch (e: Exception){
                ""
            }

        }

        fun getUniqueID():String{
            return UUID.randomUUID().toString()
        }

        fun getDeviceMake():String{
            return Build.MANUFACTURER
        }

        fun getDeviceModel():String {
            return Build.MODEL
        }

        fun getAndroidVersion():String{
            return Build.VERSION.SDK
        }

        fun updateUserInfo(data: UsersData){
            val gson = Gson()
            val json = gson.toJson(data)
            PrefsHelper.putString(Constants.Pref_UserData, json)
        }

        fun getUsersData():UsersData{
            val gson = Gson()
            val usersData = PrefsHelper.getString(Constants.Pref_UserData)
            val ud: UsersData = gson.fromJson(usersData, UsersData::class.java)
            return ud
        }

        //fun getSocialID() = "112528012612593803039"
        fun getSocialID() = getUsersData().SocialId
//        fun getSocialID()=getUsersData()?.SocialId


        fun videoCompression(path: String, desPath: String){
            val root = android.os.Environment.getExternalStorageDirectory()
            val file = File(root.absolutePath + "/FitFinder/test.mp4")

        }


    }
}