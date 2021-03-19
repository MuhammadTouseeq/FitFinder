package com.highbryds.fitfinder.ui.Chatting

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.UserMsgsAdapter
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.room.Tables.UserChat

import com.highbryds.fitfinder.vm.UserChatting.UserChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_messages_list.*

@AndroidEntryPoint
class MessagesListActivity : AppCompatActivity() {

    //    @Inject
//    lateinit var getDatabaseDao: Dao
    lateinit var adapter: UserMsgsAdapter
    var isCurrentActivity = false
    var msgList = mutableListOf<UserChat>()

    // var usersList = emptyArray<String>()
    var usersList = mutableListOf<String>()


    val userChattingViewModel: UserChattingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_list)
//        userChattingViewModel = ViewModelProvider(this, MyViewModelFactory()).get(userChattingViewModel::class.java)
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
        // userChattingViewModel.getGroupMsgs()

        userChattingViewModel.userGroupMsgs.observe(this, androidx.lifecycle.Observer {
            if (it.size > 0) {

                msgList.clear()
                adapter = UserMsgsAdapter(msgList, this)
                RV_userMsgsList.adapter = adapter

                RV_userMsgsList.visibility = View.VISIBLE
                // myObjectList.distinctBy { Pair(it.myField, it.myOtherField) }
                //   var counter = 0
                for (i in it) {

                    usersList.add(i.senderId)
                    usersList.add(i.RecipientId)
/*
                    for (j in msgList) {
                        if (i.senderId.equals(j.senderId) || i.senderId.equals(j.recipientId))
                    }*/
                    // if (msgList)

                    //  counter++
                }



                usersList = usersList.distinct().toMutableList()
                usersList.remove(KotlinHelper.getSocialID())
                var counter = 0
                for (user in usersList) {

                    var temp = 0
                    for (i in it) {

                        if (i.senderId.equals(user) || i.RecipientId.equals(user)) {
                            if (i.id > temp) {
                                temp = i.id
                                msgList.add(counter, i)

                            }


                        }


                    }
                    counter++

                }
                //     }

                //  msgList

                adapter.loadChat(msgList)
                adapter.notifyDataSetChanged()

            } else {
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