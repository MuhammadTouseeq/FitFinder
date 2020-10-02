package com.highbryds.fitfinder.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.room.Tables.UserMsgsList
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Chatting.MessageActivity
import com.highbryds.fitfinder.ui.Profile.UserStories
import com.highbryds.fitfinder.ui.StoryView.StoryFullViewActivity
import kotlinx.android.synthetic.main.activity_user_profile_main.*


class UserMsgsAdapter(var userStories: MutableList<UserMsgsList>,
                       var context: Context): RecyclerView.Adapter<UserMsgsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val userID = itemView.findViewById(R.id.userID) as TextView
        val userMSG = itemView.findViewById(R.id.userMSG) as TextView
        val CV_item = itemView.findViewById(R.id.CV_item) as CardView

        fun bindViews(userMsgsList: UserMsgsList, context: Context){

            userID.text = "User ID: ${userMsgsList.recipentID}"
            userMSG.text = "User MSG: ${userMsgsList.messageTXT}"
        }
    }

    fun addMessage(userMsgsList: UserMsgsList) {
        //    mMessages.add(new Pair(message, direction));
        userStories.add(userMsgsList)
        // notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chatlist_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(userStories[position] , context)

        holder.CV_item.setOnClickListener {
            SinchSdk.RECIPENT_ID = userStories[position].recipentID
            val intent = Intent(context , MessageActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return  userStories.size
    }


}