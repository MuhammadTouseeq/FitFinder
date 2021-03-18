package com.highbryds.fitfinder.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Chatting.MessageActivity


public class UserMsgsAdapter(var userChat: List<UserChat>?, var context: Context) :
    RecyclerView.Adapter<UserMsgsAdapter.ViewHolder>() {

    var uc: List<UserChat>? = userChat

    fun loadChat(userChat: List<UserChat>) {
        uc = userChat
        this.userChat = userChat
    }

    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var msg = "";
        val userID = itemView.findViewById(R.id.userID) as TextView
        val userMSG = itemView.findViewById(R.id.userMSG) as TextView
        val LL_item = itemView.findViewById(R.id.LL_item) as LinearLayout
        val IV_profilePic = itemView.findViewById(R.id.IV_profilePic) as ImageView
        val dateTime = itemView.findViewById(R.id.dateTime) as TextView
        val LL_card = itemView.findViewById(R.id.LL_card) as LinearLayout
        val ViewDivider = itemView.findViewById(R.id.ViewDivider) as View


        fun bindViews(userMsgsList: UserChat?, uc: List<UserChat>, pos: Int, context: Context) {

//
//            if (userMsgsList?.senderId.equals(KotlinHelper.getUsersData().SocialId)) {
//
//
//                LL_card.visibility = View.GONE
//                ViewDivider.visibility = View.GONE
//
//            } else {
//
//
//                msg = userMsgsList!!.message
//
//
//                val ob = uc.filter {
//                    it.senderId.equals(userMsgsList?.senderId) and it.recipientId.equals(
//                        userMsgsList?.recipientId
//                    )
//                }.lastOrNull()
//
//                val ob1 = uc.filter {
//                    it.recipientId.equals(userMsgsList?.senderId) and it.senderId.equals(
//                        userMsgsList?.recipientId
//                    )
//                }.lastOrNull()
//
//                if (ob != null && ob1 != null) {
//                    if (ob!!.id > ob1!!.id) {
//                        // ob is greater
//
//                        msg = ob.message
//
//                    } else {
//                        // ob1 is greater
//                        msg = ob1.message
//
//                    }
//
//                }



                LL_card.visibility = View.VISIBLE
                ViewDivider.visibility = View.VISIBLE
                userID.text = if (KotlinHelper.getSocialID().equals(userMsgsList?.senderId)) userMsgsList?.recipientName else userMsgsList?.senderName
                if (userMsgsList!!.isRead) {
                    userMSG.setTypeface(null, Typeface.NORMAL);
                } else {
                    userMSG.setTypeface(null, Typeface.BOLD);
                }
                userMSG.text = userMsgsList?.message

                dateTime.text = KotlinHelper.getTimeDifference(userMsgsList?.timeStamp!!)

                Glide
                    .with(context)
                    .load(userMsgsList?.recipientImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(IV_profilePic);


            }


        }

//        }


//    fun addMessage(userMsgsList: MutableList<UserMsgsList>) {
//        //    mMessages.add(new Pair(message, direction));
//        userStories.add(userMsgsList)
//        // notifyDataSetChanged();
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chatlist_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(userChat!![position], uc!!, position, context)

        holder.LL_item.setOnClickListener {
            SinchSdk.RECIPENT_ID = userChat!![position].senderId
            SinchSdk.RECIPENT_NAME = userChat!![position].senderName
            SinchSdk.RECIPENT_IMG = userChat!![position].recipientImage
            val intent = Intent(context, MessageActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        if (userChat != null) {
            return userChat!!.size
        } else {
            return 0
        }
    }


}