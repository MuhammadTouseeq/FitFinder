package com.highbryds.fitfinder.commonHelper

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.highbryds.fitfinder.callbacks.onConfirmListner
import com.highbryds.fitfinder.model.UsersData
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class KotlinHelper {
    companion object {
        fun isJsonObjNull(obj: JSONObject, value: String): String {
            return try {
                if (obj.getString(value) != null) {
                    obj.getString(value).toString()
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }

        }

        fun getUniqueID(): String {
            return UUID.randomUUID().toString()
        }

        fun getDeviceMake(): String {
            return Build.MANUFACTURER
        }

        fun getDeviceModel(): String {
            return Build.MODEL
        }

        fun getAndroidVersion(): String {
            return Build.VERSION.SDK
        }

        fun updateUserInfo(data: UsersData) {
            val gson = Gson()
            val json = gson.toJson(data)
            PrefsHelper.putString(Constants.Pref_UserData, json)
        }

        fun getUsersData(): UsersData {
            val gson = Gson()
            val usersData = PrefsHelper.getString(Constants.Pref_UserData)
            val ud: UsersData = gson.fromJson(usersData, UsersData::class.java)
            return ud
        }

        //fun getSocialID() = "112528012612593803039"
        fun getSocialID() = getUsersData().SocialId
//        fun getSocialID()=getUsersData()?.SocialId


        fun videoCompression(path: String, desPath: String) {
            val root = android.os.Environment.getExternalStorageDirectory()
            val file = File(root.absolutePath + "/FitFinder/test.mp4")

        }

        fun alertDialog(
            title: String,
            msg: String,
            context: Context,
            confirmListner: onConfirmListner
        ) {
            val builder = AlertDialog.Builder(context)
            //set title for alert dialog
            builder.setTitle(title)
            //set message for alert dialog
            builder.setMessage(msg)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            // Create the AlertDialog
            var alertDialog: AlertDialog? = null

            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                //logoutSocialAccount(context)
                confirmListner.onClick()
            }

            builder.setNegativeButton("No") { dialogInterface, which ->
                alertDialog?.cancel()
            }

            alertDialog = builder.create()

            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }


        fun logoutSocialAccount(context: Context) {
            if (getUsersData().SocialType.equals("FB", false)) {
                LoginManager.getInstance().logOut()
            } else {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
            }
        }


        fun getTimeDifference(msgDate: String): String {


            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

            val msgDate: Date = parser.parse(msgDate)


            val miliSeconds: Long = Calendar.getInstance().timeInMillis - msgDate.time


            val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(miliSeconds)
            val minute = seconds / 60
            val hour = minute / 60
            val days = hour / 24
            var time_ago = ""
            if (days > 0) {
                if (days < 2) {
                    time_ago = "$days day ago"
                } else {
                    // only show days
                    time_ago = "$days days ago"
                }


            } else if (hour > 0) {
                //only show hour
                if (hour < 2) {

                    time_ago = "$hour hour ago"

                } else {

                    time_ago = "$hour hours ago"

                }

            } else if (minute > 0) {
                if (minute < 2) {

                    time_ago = "$minute  minute ago"

                } else {

                    time_ago = "$minute minutes ago"

                }
                // only show minutes


            } else if (seconds > 0) {
                if (seconds < 2) {

                    time_ago = "$seconds second ago"

                } else {

                    time_ago = "$seconds seconds ago"

                }
                // only show seconds


            }
            return time_ago

        }

        fun getMeaningFullTime(msgDate: String): String {
            val currentFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val converterFormat = SimpleDateFormat("EEE, MMM d, ''yy  h:mm a")
            val msgDateNew: Date = currentFormat.parse(msgDate)
            val formats1: String = converterFormat.format(msgDateNew)
            return formats1

        }
    }


}