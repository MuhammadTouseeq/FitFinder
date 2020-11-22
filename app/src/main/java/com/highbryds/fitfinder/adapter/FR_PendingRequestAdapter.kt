package com.highbryds.fitfinder.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.bestmatch
import com.highbryds.fitfinder.model.carpool.PendingRequestModel
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Chatting.MessageActivity

class FR_PendingRequestAdapter
    (
    var frSearchcar: PendingRequestModel, var context: Context, var listtype: Int) :
    RecyclerView.Adapter<FR_PendingRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById(R.id.name) as TextView
        val CarnColor = itemView.findViewById(R.id.CarnColor) as TextView
        val leavingTimenLocation = itemView.findViewById(R.id.leavingTimenLocation) as TextView
        val endTimenLocation = itemView.findViewById(R.id.endTimenLocation) as TextView
        val ac = itemView.findViewById(R.id.ac) as TextView
        val price = itemView.findViewById(R.id.price) as TextView
        val seats = itemView.findViewById(R.id.seats) as TextView
        val requestRide = itemView.findViewById(R.id.cancelRide) as Button
        val chatUser = itemView.findViewById(R.id.chatUser) as TextView

        fun bindViews(frSearchcar: FR_SearchCar, context: Context) {

            name.text = frSearchcar.cellNumber
            CarnColor.text = "${frSearchcar.carmake} ${frSearchcar.carmodel} | ${frSearchcar.color}"
            leavingTimenLocation.text = "At: ${
                JavaHelper.parseDateToFormat(
                    "yyyy-MM-ddTHH:mm:ss.SSSZ",
                    frSearchcar.startingtime
                )
            } From: ${frSearchcar.starting_point.name}"
            endTimenLocation.text = "To: ${frSearchcar.destination.name}"
            ac.text = "AC:\n${frSearchcar.aC}"
            price.text = "Rs:\n${frSearchcar.preferredcost_min}"
            seats.text = "Seats:\n${frSearchcar.seatsleft}/${frSearchcar.totalseats}"


            chatUser.setOnClickListener {

                SinchSdk.RECIPENT_ID = frSearchcar.user_data?.SocialId
                SinchSdk.RECIPENT_NAME = frSearchcar.user_data?.name
                SinchSdk.USER_ID = KotlinHelper.getUsersData().SocialId

                val intent = Intent(context, MessageActivity::class.java)
                context.startActivity(intent)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.fr_pendinglist_item,
            parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindViews(frSearchcar.carpooldata.get(position), context)

        holder.requestRide.setOnClickListener {
           context.toast(context, "Cancelling Request")
        }
    }

    override fun getItemCount(): Int {
         return frSearchcar.carpooldata.size
    }

}