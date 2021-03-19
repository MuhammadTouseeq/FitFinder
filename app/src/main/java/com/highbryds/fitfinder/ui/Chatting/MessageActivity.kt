package com.highbryds.fitfinder.ui.Chatting

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.MessageAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.Chatting
import com.highbryds.fitfinder.room.Dao
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.vm.UserChatting.UserChattingViewModel
import com.sinch.android.rtc.SinchClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_message.*
import javax.inject.Inject


private var mMessageAdapter: MessageAdapter? = null
var instance: SinchClient? = null
var uc: UserChat? = null
lateinit var context: Context

@AndroidEntryPoint
class MessageActivity : AppCompatActivity(), ApiResponseCallBack {

    @Inject
    lateinit var getDatabaseDao: Dao


    val userChattingViewModel: UserChattingViewModel by viewModels()

    lateinit var textBody: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)


        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = SinchSdk.RECIPENT_NAME
        toolbar.setNavigationOnClickListener {
            finish()
        }

        userChattingViewModel.apiResponseCallBack = this
        //name.text = SinchSdk.RECIPENT_NAME
        //SinchSdk.USER_ID = KotlinHelper.getUsersData().SocialId
        context = this
        instance = SinchSdk.getInstance(this)
        // instance!!.callClient.addCallClientListener(SinchCallClientListener())

        userChattingViewModel.getMSG(SinchSdk.RECIPENT_ID, SinchSdk.USER_ID)
        mMessageAdapter = MessageAdapter(
            this,
            this,
            getDatabaseDao.getallChat(SinchSdk.RECIPENT_ID, SinchSdk.USER_ID)?.value
        )
        // mMessageAdapter = MessageAdapter(this,this, getDatabaseDao.getallChat()?.value)
        val messagesList = findViewById<View>(R.id.lstMessages) as ListView
        messagesList.adapter = mMessageAdapter


        userChattingViewModel.userMsgs.observe(this, Observer {
            mMessageAdapter!!.loadChat(it)
            mMessageAdapter!!.notifyDataSetChanged()
        })

        userChattingViewModel.msgTo.observe(this, Observer {
            uc = UserChat()
            uc!!.recipientImage = it[0].imageUrl
            uc!!.recipientName = it[0].name
            uc!!.senderName = KotlinHelper.getUsersData().name
            uc!!.messageId = "0"
            uc!!.message = textBody
            uc!!.recipientId = SinchSdk.RECIPENT_ID
            uc!!.senderId = SinchSdk.USER_ID
            uc!!.type = 1
            uc!!.timeStamp = JavaHelper.getDateTimeSeconds()
            uc!!.setRead(true)
            insertChatMessages(uc)

        })


        btnSend.setOnClickListener {
            sendMessage()
        }


    }

    fun sendMessage() {

        if (!txtTextBody.text.toString().isEmpty()) {
            textBody = txtTextBody.getText().toString()
            val chattingViewModel = Chatting(
                textBody,
                SinchSdk.RECIPENT_ID,
                KotlinHelper.getUsersData().name,
                KotlinHelper.getUsersData().imageUrl,
                JavaHelper.getDateTimeSeconds(),
                KotlinHelper.getUsersData().SocialId
            )

            // FirebaseInstanceId.getInstance().getToken()
            userChattingViewModel.sendChat(chattingViewModel)
            txtTextBody.setText("")
        } else {
            this.toast(this, "Please write something")
        }
    }


    fun insertChatMessages(uc: UserChat?) {
        getDatabaseDao.insertItem(uc)
        textBody = ""
    }


    override fun getError(error: String) {
        this.toast(this, error.toString())
    }

    override fun getSuccess(success: String) {
        this.toast(this, success.toString())
    }

    override fun onResume() {
        super.onResume()
        getDatabaseDao.updateReadStatus(SinchSdk.RECIPENT_ID)
    }
}