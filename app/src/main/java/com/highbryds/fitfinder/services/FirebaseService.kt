package com.highbryds.fitfinder.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.*
import com.highbryds.fitfinder.room.Dao
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.ui.Main.HomeMapActivity
import com.highbryds.fitfinder.utils.LocationHelper
import com.highbryds.fitfinder.utils.NotificationData
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : FirebaseMessagingService() {

    @Inject
    lateinit var getDatabaseDao: Dao

    @Override
    override fun onNewToken(token: String) {
          super.onNewToken(token)
        Log.d("NEW_TOKEN", token);
      //  Log.d(TAG, "Refreshed token: $token")

        PrefsHelper.putString(Constants.Pref_DeviceToken, token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        // chatting notifications
        val obj: String? = p0.getData().get("type")
        if (obj != null && obj.equals("chat")) {

            //val id = Integer.valueOf(obj.toString())
            // PrefsHelper.putString("FCMFCM" , "TRUE")
            // PrefsHelper.putString("FCMFCM2" , p0.getData().get("message"))
            val msg = p0.getData().get("message") + "\n" + p0.getData().get("messageTime")
            val name = p0.getData().get("messageFromName")
            sendNotification(msg, name!!)

            val uc = UserChat()
            uc.messageId = "0"
            uc.recipientImage = p0.getData().get("messageFromProfile")
            uc.recipientName = KotlinHelper.getUsersData().name
            uc.senderName = p0.getData().get("messageFromName")
            uc.message = p0.getData().get("message")
            uc.recipientId = KotlinHelper.getUsersData().SocialId
            uc.senderId = p0.getData().get("messageFromSocialId")
            uc.type = 0
            uc.timeStamp = p0.getData().get("messageTime")
            uc.setRead(false)
            insertChatMessages(uc)

        } else if (obj != null && obj.equals("help")) {
            try {
                var data = emptyList<String>()
                data = p0.getData().get("message")!!.split("|")
                if (!data[2].equals(KotlinHelper.getUsersData().SocialId)) {
                    val locationHelper = LocationHelper()
                    locationHelper.LocationInitialize(getApplicationContext())
                    val distance = JavaHelper.calculateDistance(
                        LatLng(
                            data[1].split(",")[0].toDouble(),
                            data[1].split(",")[1].toDouble()
                        ), LatLng(PrefsHelper.getDouble("LAT"), PrefsHelper.getDouble("LNG"))
                    )
                    if (distance <= 2.5) {
                        PrefsHelper.putString(
                            Constants.Pref_ToOpenStoryAuto,
                            p0.getData().get("storyid")
                        )
                        sendNotification(data[0], "Help Alert")
                    }
                }

            } catch (e: Exception) {
            }
        } else {

            val data: Map<String, String> = p0.getData()
            // handle backend
            if (data.get("type") != null) {
                PrefsHelper.putString(Constants.Pref_ToOpenStoryAuto, data.get("storyid"))
                val title = data.get("title")
                val des = data.get("body")
                val img = data.get("img")

                if (img.equals("") || img == null) {
                    sendNotification(des!!, title!!)
                } else {
                    NotificationData.title = title
                    NotificationData.content = des
                    NotificationData.imageUrl = img
                    NotificationData.imageFeedUrl = img

                    getImage()
                }

            } else {
                // To handle firebase console notification with image or without image
                val msg: String = p0.getNotification()?.getBody()!!
                val title: String = p0.getNotification()?.getTitle()!!
                val image = if (p0.getNotification()?.getImageUrl()
                        .toString() == "null"
                ) "null" else p0.getNotification()?.getImageUrl().toString()


                if (image == "null") {
                    sendNotification(msg, title)
                } else {
                    NotificationData.title = title
                    NotificationData.content = msg
                    NotificationData.imageUrl = image
                    NotificationData.imageFeedUrl = image

                    getImage()
                }

            }
        }
    }


    private fun getImage() {
        val uiHandler = Handler(Looper.getMainLooper())
        uiHandler.post { // Get image from data Notification
            Picasso.get()
                .load(NotificationData.imageUrl)
                .into(target)
        }
    }

    var target: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            sendNotification(bitmap)
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }

    private fun sendNotification(bitmap: Bitmap) {
        val style = NotificationCompat.BigPictureStyle()
        style.bigPicture(bitmap)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val intent = Intent(getApplicationContext(), HomeMapActivity::class.java);

        //Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "101"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notification",
                NotificationManager.IMPORTANCE_MAX
            )

            //Configure Notification Channel
            notificationChannel.description = "FitFinder"
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(NotificationData.title)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentText(NotificationData.content)
            .setContentIntent(pendingIntent)
            .setStyle(style)
            .setLargeIcon(bitmap)
            .setWhen(System.currentTimeMillis())
            .setPriority(Notification.PRIORITY_MAX)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun sendNotification(message: String, title: String) {
        val GROUP_KEY_ALERTS = "com.highbryds.fitfinder.ALERTS"
        val intent: Intent

        intent = Intent(getApplicationContext(), HomeMapActivity::class.java);

        val nc = NotificationClass()
        val notifyID = 3
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Creating Channel
            nc.createMainNotificationChannel(applicationContext)
            //building Notification.
            val notifi = Notification.Builder(
                applicationContext, nc.getMainNotificationId()
            )
            notifi.setSmallIcon(R.drawable.clap_icon)
            notifi.setContentTitle(title)
            notifi.setContentText(message)
            notifi.setAutoCancel(true)
            notifi.setContentIntent(pendingIntent)
            notifi.setGroup(GROUP_KEY_ALERTS)
            notifi.style = Notification.BigTextStyle()
                .bigText(message)
            notifi.setSound(alarmSound, AudioAttributes.USAGE_NOTIFICATION)


            //getting notification object from notification builder.
            val n = notifi.build()
            val mNotificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(notifyID, n)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //for devices less than API Level 26
                val notification: Notification = Notification.Builder(
                    applicationContext
                )
                    .setContentTitle("FitFinder")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.clap_icon)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(message)
                    )
                    .setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_DEFAULT)
                    .setGroup(GROUP_KEY_ALERTS)
                    .build()
                val mNotificationManager =
                    applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.notify(notifyID, notification)
            } else {

                //for devices less than API Level 26
                val notification: Notification = Notification.Builder(
                    applicationContext
                )
                    .setContentTitle("FitFinder")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.clap_icon)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(message)
                    )
                    .setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_DEFAULT)
                    .build()
                val mNotificationManager =
                    applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.notify(notifyID, notification)
            }
        }
    }

    fun insertChatMessages(uc: UserChat?) {
        getDatabaseDao.insertItem(uc)
    }
}