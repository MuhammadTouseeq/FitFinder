package com.highbryds.fitfinder.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.GeneralCallBack
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.carpool.PendingRequestModel
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Chatting.MessageActivity

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
        val source = itemView.findViewById(R.id.source) as TextView

        val endTimenLocation = itemView.findViewById(R.id.endTimenLocation) as TextView
        val ac = itemView.findViewById(R.id.ac) as TextView
        val price = itemView.findViewById(R.id.price) as TextView
        val seats = itemView.findViewById(R.id.seats) as TextView
        val requestRide = itemView.findViewById(R.id.cancelRide) as Button
        val completeRide = itemView.findViewById(R.id.completeRide) as Button
        val chatUser = itemView.findViewById(R.id.chatUser) as TextView
        val cell = itemView.findViewById(R.id.cell) as TextView

        // val time = itemView.findViewById(R.id.time) as TextView
        val driverRating = itemView.findViewById(R.id.driverRating) as RatingBar

        //   val CV_pendingItem = itemView.findViewById(R.id.CV_pendingItem) as CardView
        val user_profile = itemView.findViewById(R.id.user_profile) as ImageView

        fun bindViews(frSearchcar: FR_SearchCar?, carreq: FR_SearchCar, context: Context) {


            //carreq.status
            if (carreq.status.equals("accepted")) {
                //removing 5 hour
                if ((JavaHelper.dateTimeMilli(frSearchcar?.startingtime)) > System.currentTimeMillis()) {
                    requestRide.visibility = View.VISIBLE
                    completeRide.visibility = View.GONE
                } else {
                    completeRide.visibility = View.VISIBLE
                    requestRide.visibility = View.GONE
                }
            } else {
                completeRide.visibility = View.GONE
                requestRide.visibility = View.VISIBLE

            }


            name.text = frSearchcar!!.regno
            cell.text =
                "${frSearchcar!!.user_data?.name} ( ${frSearchcar!!.user_data?.cellNumber} )"
            driverRating.rating = frSearchcar!!.user_data?.rating!!

            CarnColor.text = "${frSearchcar.carmake} ${frSearchcar.carmodel} | ${frSearchcar.color}"
            leavingTimenLocation.text = "${

                KotlinHelper.getTimeDifference(frSearchcar.startingtime, "noSeconds")


            } (${KotlinHelper.getMeaningFullTimeForCarPool(frSearchcar.startingtime)})".replace(
                "ago",
                ""
            )

            source.text = frSearchcar.starting_point.name
            endTimenLocation.text = "${frSearchcar.destination.name}"
            //ac.text = "AC:${frSearchcar.aC}"
            price.text = "Rs. ${frSearchcar.preferredcost_min}"
            //    seats.text = "Available seats:${frSearchcar.seatsleft}/${frSearchcar.totalseats}"
            // seats.text = "${frSearchcar.seatsleft} seats available"


            source.setOnClickListener {

                val intentUri =
                    Uri.parse("geo:${frSearchcar.starting_point.latitude},${frSearchcar.starting_point.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)

            }

            endTimenLocation.setOnClickListener {

                val intentUri =
                    Uri.parse("geo:${frSearchcar.destination.latitude},${frSearchcar.destination.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)

            }

            endTimenLocation.setOnClickListener {

                val intentUri =
                    Uri.parse("geo:${frSearchcar.destination.latitude},${frSearchcar.destination.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)

            }


            cell.setOnClickListener {

                val u = Uri.parse("tel:" + frSearchcar!!.user_data?.cellNumber)

                val i = Intent(Intent.ACTION_DIAL, u)
                context.startActivity(i)
            }



            Glide
                .with(context)
                .load(frSearchcar.user_data?.imageUrl)
                .placeholder(R.drawable.user_profile)
                .into(user_profile);

            if (frSearchcar.aC.equals("YES")) {

                seats.text =
                    "${frSearchcar.seatsleft}/${frSearchcar.totalseats} seats (with AC)"

            } else {
                seats.text =
                    "${frSearchcar.seatsleft}/${frSearchcar.totalseats} seats (without AC)"


            }
            /*    name.text = frSearchcar!!.cellNumber
                CarnColor.text = "${frSearchcar.carmake} ${frSearchcar.carmodel} | ${frSearchcar.color}"
                time.text = "At: ${JavaHelper.parseDateToFormat("yyyy-MM-ddTHH:mm:ss.SSSZ", frSearchcar.startingtime)}"
                leavingTimenLocation.text =
                 "From: ${frSearchcar.starting_point.name}"
                endTimenLocation.text = "\nTo: ${frSearchcar.destination.name}"
                ac.text = "AC:\n${frSearchcar.aC}"
                price.text = "Rs:\n${frSearchcar.preferredcost_min}"
                seats.text = "Seats:\n${frSearchcar.seatsleft}/${frSearchcar.totalseats}"*/


            chatUser.setOnClickListener {

                SinchSdk.RECIPENT_ID = frSearchcar.user_data?.SocialId
                SinchSdk.RECIPENT_NAME = frSearchcar.user_data?.name
                SinchSdk.USER_ID = KotlinHelper.getUsersData().SocialId

                val intent = Intent(context, MessageActivity::class.java)
                context.startActivity(intent)
            }


            /*  CV_pendingItem.setOnClickListener{
                  val intentUri = Uri.parse("geo:${frSearchcar.starting_point.latitude},${frSearchcar.starting_point.longitude}")
                  val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
                  mapIntent.setPackage("com.google.android.apps.maps")
                  context.startActivity(mapIntent)
              }*/


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

        holder.bindViews(
            frSearchcar.carpooldata?.get(position),
            frSearchcar.carrequestdata?.get(position)!!,
            context
        )

        holder.requestRide.setOnClickListener {
            generalCallBack.eventOccur(frSearchcar.carrequestdata!!.get(position).id!!)

        }

        holder.completeRide.setOnClickListener {
            generalCallBack.eventOccurCancelRideRating(
                frSearchcar.carrequestdata!!.get(position).id!!,
                frSearchcar.carpooldata!!.get(position).socialId
            )
        }
    }

    override fun getItemCount(): Int {
        return frSearchcar.carpooldata!!.size
    }

}