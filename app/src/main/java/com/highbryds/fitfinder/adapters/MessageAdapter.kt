package com.highbryds.fitfinder.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.room.Tables.UserChat

class MessageAdapter constructor(activity: Activity, var context: Context, uc: List<UserChat>?) :
    BaseAdapter() {

    companion object {
        val DIRECTION_INCOMING = 0

        val DIRECTION_OUTGOING = 1
    }

    //private List<Pair<Message, Integer>> mMessages;
    private var mMessagesnew: List<UserChat>? = uc


    private var mInflater: LayoutInflater? = activity.getLayoutInflater();

    fun loadChat(userChat: List<UserChat>) {
        mMessagesnew = userChat
    }

    override fun getCount(): Int {
        return if (mMessagesnew != null) {
            mMessagesnew!!.size
        } else 0
    }

    override fun getItem(i: Int): Any? {
        return mMessagesnew!![i]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return mMessagesnew!![position].getType()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup?): View? {
        //ViewH
        var convertView = convertView
        val direction = getItemViewType(i)
        Log.d("##", "this is : $direction")
        var res = 0
        if (convertView == null) {
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.item_chat_right
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.item_chat_left
            }
            convertView = mInflater!!.inflate(res, viewGroup, false)
        }
        val txtMessage = convertView!!.findViewById<TextView>(R.id.txtMessage)
        val txtDate = convertView.findViewById<TextView>(R.id.txtDate)

        if (direction == DIRECTION_INCOMING) {
            txtMessage.setTextColor(context.resources.getColor(R.color.colorBlack))

            // txtDate.setTextColor(context.resources.getColor(R.color.colorBlack))
        } else {
            txtMessage.setTextColor(context.resources.getColor(R.color.colorWhite))
            // txtDate.setTextColor(context.resources.getColor(R.color.colorWhite))
        }
        txtMessage.text = mMessagesnew!![i].getMessage().split("~".toRegex()).toTypedArray()[0]
        txtDate.setText(KotlinHelper.getMeaningFullTime(mMessagesnew!![i].TimeStamp))
        return convertView
    }
}