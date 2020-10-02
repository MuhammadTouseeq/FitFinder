package com.highbryds.fitfinder.ui.Chatting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.MessageAdapter
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.room.Dao
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.sinch.SinchSdk
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.SinchClient
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallClient
import com.sinch.android.rtc.calling.CallClientListener
import com.sinch.android.rtc.messaging.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_message.*
import javax.inject.Inject


private var mMessageAdapter: MessageAdapter? = null
var instance: SinchClient? = null
var uc: UserChat? = null
lateinit var context: Context

@AndroidEntryPoint
class MessageActivity : AppCompatActivity(), MessageClientListener {

    @Inject
    lateinit var getDatabaseDao: Dao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)


        name.text = "Yahan Name Ayega Bhai"
        //SinchSdk.USER_ID = KotlinHelper.getUsersData().SocialId
        context = this
        instance = SinchSdk.getInstance(this)
        instance!!.callClient.addCallClientListener(SinchCallClientListener())
        mMessageAdapter = MessageAdapter(this,this, getDatabaseDao.getallChat(SinchSdk.RECIPENT_ID, SinchSdk.USER_ID))
        val messagesList = findViewById<View>(R.id.lstMessages) as ListView
        messagesList.adapter = mMessageAdapter

        SinchSdk.getInstance(this)!!.messageClient.addMessageClientListener(this)

        btnSend.setOnClickListener {
            sendMessage()
        }


        btnCall.setOnClickListener {
            requestAudio()
        }

        btnVideo.setOnClickListener {
            requestCameraPermission();
        }


    }

    fun sendMessage() {

        val textBody: String = txtTextBody.getText().toString()
        if (textBody.isEmpty()) {
            Toast.makeText(applicationContext, "No text message", Toast.LENGTH_SHORT).show()
            return
        }
        val message = WritableMessage(SinchSdk.RECIPENT_ID, textBody)
        SinchSdk.getInstance(applicationContext)!!.messageClient.send(message)
        txtTextBody.setText("")
    }

    override fun onIncomingMessage(p0: MessageClient?, p1: Message?) {
        setMessages(p1!!, MessageAdapter.DIRECTION_INCOMING)
    }

    override fun onMessageSent(p0: MessageClient?, p1: Message?, p2: String?) {
        Log.d("MESSAGEActivity", p1!!.textBody)
        setMessages(p1!!, MessageAdapter.DIRECTION_OUTGOING)
    }

    override fun onMessageFailed(p0: MessageClient?, p1: Message?, p2: MessageFailureInfo?) {
        this.toast(this, "User Not Found")
        Log.d("MESSAGEACTIVITY", "FAILED")
    }

    override fun onMessageDelivered(p0: MessageClient?, p1: MessageDeliveryInfo?) {
        Log.d("MESSAGEACTIVITY", "DELIVERED")
    }

    override fun onShouldSendPushData(
        p0: MessageClient?,
        p1: Message?,
        p2: MutableList<PushPair>?
    ) {
        TODO("Not yet implemented")
    }

    private class SinchCallClientListener : CallClientListener {
        override fun onIncomingCall(callClient: CallClient, call: Call) {
            var intent: Intent? = null
            if (call.details.isVideoOffered) {
                intent = Intent(context, IncomingCallScreenActivity::class.java)
            } else {
                intent = Intent(context, CallScreenActivity::class.java)
            }
            intent!!.putExtra(SinchSdk.CALL_ID, call.callId)
            context.startActivity(intent)
        }
    }

    fun setMessages(message: Message, type: Int) {
        if (getDatabaseDao.getMessageCount(message.messageId) == 0) {
            uc = UserChat()
            uc!!.messageId = message.messageId
            uc!!.message = message.textBody
            uc!!.recipientId = message.senderId
            //uc!!.senderId = SinchSdk.USER_ID
            uc!!.type = type
            uc!!.timeStamp = message.timestamp.time
            insertChatMessages(uc)
            mMessageAdapter!!.addMessage(uc!!)
            mMessageAdapter!!.notifyDataSetChanged()
        }
    }

    fun insertChatMessages(uc: UserChat?) {
        getDatabaseDao.insertItem(uc)
    }

    private fun CallUser() {
        val call =
            SinchSdk.getInstance(applicationContext).callClient.callUser(SinchSdk.RECIPENT_ID)
        val callId = call.callId
        val callScreen = Intent(context, CallScreenActivity::class.java)
        callScreen.putExtra(SinchSdk.CALL_ID, callId)
        startActivity(callScreen)
    }

    private fun requestAudio() {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        CallUser()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).onSameThread()
            .check()
    }

    private fun requestCameraPermission() {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.VIBRATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        videoCallUser()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).onSameThread()
            .check()
    }


    private fun videoCallUser() {
        val call =
            SinchSdk.getInstance(applicationContext).callClient.callUserVideo(SinchSdk.RECIPENT_ID)
        val callId = call.callId
        val callScreen = Intent(context, CallScreenActivity::class.java)
        callScreen.putExtra(SinchSdk.CALL_ID, callId)
        startActivity(callScreen)
    }

}