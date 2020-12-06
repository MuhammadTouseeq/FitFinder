package com.highbryds.fitfinder.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.GeneralCallBack
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.bestmatch
import com.highbryds.fitfinder.model.carpool.PendingRequestModel
import com.highbryds.fitfinder.model.carpool.RIDE_STATUS
import com.highbryds.fitfinder.model.carpool.RideStatus
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Chatting.MessageActivity
import com.mikepenz.fastadapter.dsl.genericFastAdapter

class FR_PendingRequestAdapter
    (
    var frSearchcar: PendingRequestModel, var context: Context, var listtype: Int,
    val generalCallBack: GeneralCallBack
) :
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
        val completeRide = itemView.findViewById(R.id.completeRide) as Button
        val chatUser = itemView.findViewById(R.id.chatUser) as TextView
        val time = itemView.findViewById(R.id.time) as TextView
        val CV_pendingItem = itemView.findViewById(R.id.CV_pendingItem) as CardView

        fun bindViews(frSearchcar: FR_SearchCar?, carreq: FR_SearchCar, context: Context) {


            //carreq.status
            if (carreq.status.equals("accepted")){
                if ((JavaHelper.dateTimeMilli(frSearchcar?.startingtime)+(5*60*60*1000)) >  System.currentTimeMillis()){
                    requestRide.visibility = View.VISIBLE
                    completeRide.visibility = View.GONE
                }else{
                    completeRide.visibility = View.VISIBLE
                    requestRide.visibility = View.GONE
                }
            }else{
                completeRide.visibility = View.GONE
                requestRide.visibility = View.VISIBLE

            }

            name.text = frSearchcar!!.cellNumber
            CarnColor.text = "${frSearchcar.carmake} ${frSearchcar.carmodel} | ${frSearchcar.color}"
            time.text = "At: ${JavaHelper.parseDateToFormat("yyyy-MM-ddTHH:mm:ss.SSSZ", frSearchcar.startingtime)}"
            leavingTimenLocation.text =
             "From: ${frSearchcar.starting_point.name}"
            endTimenLocation.text = "\nTo: ${frSearchcar.destination.name}"
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


            CV_pendingItem.setOnClickListener{
                val intentUri = Uri.parse("geo:${frSearchcar.starting_point.latitude},${frSearchcar.starting_point.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
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

        holder.bindViews(frSearchcar.carpooldata?.get(position), frSearchcar.carrequestdata?.get(position)!!, context)

        holder.requestRide.setOnClickListener {
            generalCallBack.eventOccur(frSearchcar.carrequestdata!!.get(position).id!!)

        }

        holder.completeRide.setOnClickListener {
            generalCallBack.eventOccurCancelRideRating(frSearchcar.carrequestdata!!.get(position).id!!  ,
                frSearchcar.carpooldata!!.get(position).socialId)
        }
    }

    override fun getItemCount(): Int {
        return frSearchcar.carpooldata!!.size
    }

}