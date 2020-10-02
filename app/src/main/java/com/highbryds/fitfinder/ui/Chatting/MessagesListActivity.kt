package com.highbryds.fitfinder.ui.Chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.UserMsgsAdapter
import com.highbryds.fitfinder.adapters.MessageAdapter
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.room.Dao
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.room.Tables.UserMsgsList
import com.highbryds.fitfinder.sinch.SinchSdk
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallClient
import com.sinch.android.rtc.calling.CallClientListener
import com.sinch.android.rtc.messaging.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_messages_list.*
import javax.inject.Inject

@AndroidEntryPoint
class MessagesListActivity : AppCompatActivity(), MessageClientListener {

    @Inject
    lateinit var getDatabaseDao: Dao
    lateinit var adapter: UserMsgsAdapter
    var isCurrentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_list)

        RV_userMsgsList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        setList()


        context = this
        instance = SinchSdk.getInstance(this)
        instance!!.callClient.addCallClientListener(SinchCallClientListener())
        SinchSdk.getInstance(this)!!.messageClient.addMessageClientListener(this)
    }

    override fun onResume() {
        super.onResume()
        isCurrentActivity = true
    }

    override fun onPause() {
        super.onPause()
        isCurrentActivity = false
    }

    override fun onIncomingMessage(p0: MessageClient?, p1: Message?) {
        Log.d("MESSAGELISt" , p1!!.textBody)
        val userMsgsList = UserMsgsList(0 , p1.messageId , p1.textBody, p1.senderId , p1.timestamp.time)
        getDatabaseDao.insertMsgsList(userMsgsList)
        adapter.addMessage(userMsgsList)
        adapter.notifyDataSetChanged()
        if (isCurrentActivity){
            setMessages(p1 , MessageAdapter.DIRECTION_INCOMING)
        }

    }

    override fun onMessageSent(p0: MessageClient?, p1: Message?, p2: String?) {
        Log.d("MESSAGELISt" , p1!!.textBody)
        if (isCurrentActivity){
            setMessages(p1 , MessageAdapter.DIRECTION_OUTGOING)
        }

    }

    override fun onMessageFailed(p0: MessageClient?, p1: Message?, p2: MessageFailureInfo?) {
       this.toast(this , p2.toString())
    }

    override fun onMessageDelivered(p0: MessageClient?, p1: MessageDeliveryInfo?) {
        this.toast(this , p1.toString())
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

    private fun setList(){
        val list: MutableList<UserMsgsList> = getDatabaseDao.getmsgs()
        adapter = UserMsgsAdapter(list,this)
        RV_userMsgsList.adapter = adapter
    }


    fun setMessages(message: Message, type: Int) {
        if (getDatabaseDao.getMessageCount(message.messageId) == 0) {
            uc = UserChat()
            uc!!.messageId = message.messageId
            uc!!.message = message.textBody
            uc!!.recipientId = message.recipientIds.get(0)
            uc!!.senderId = message.senderId
            uc!!.type = type
            uc!!.timeStamp = message.timestamp.time
            insertChatMessages(uc)
        }
    }

    fun insertChatMessages(uc: UserChat?) {
        getDatabaseDao.insertItem(uc)
    }




}