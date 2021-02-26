package com.highbryds.fitfinder.ui.Chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.common.StringUtils
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.UserMsgsAdapter
import com.highbryds.fitfinder.adapters.MessageAdapter
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.room.Dao
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.room.Tables.UserMsgsList
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.vm.UserChatting.UserChattingViewModel
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallClient
import com.sinch.android.rtc.calling.CallClientListener
import com.sinch.android.rtc.messaging.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_messages_list.*
import java.util.Observer
import javax.inject.Inject

@AndroidEntryPoint
class MessagesListActivity : AppCompatActivity() {

    @Inject
    lateinit var getDatabaseDao: Dao
    lateinit var adapter: UserMsgsAdapter
    var isCurrentActivity = false
    @Inject
    lateinit var userChattingViewModel: UserChattingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_list)

        context = this
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = "Messages"
        toolbar.setNavigationOnClickListener {
            finish()
        }


        RV_userMsgsList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        userChattingViewModel.getGroupMsgs()
        adapter = UserMsgsAdapter( getDatabaseDao.getmsgs().value , this)
        RV_userMsgsList.adapter = adapter
        userChattingViewModel.userGroupMsgs.observe(this , androidx.lifecycle.Observer {
            if (it.size > 0){
                RV_userMsgsList.visibility = View.VISIBLE
                adapter.loadChat(it)
                adapter.notifyDataSetChanged()
            }else{
                RV_userMsgsList.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }

        })


    }

    override fun onResume() {
        super.onResume()
        isCurrentActivity = true
    }

    override fun onPause() {
        super.onPause()
        isCurrentActivity = false
    }

}