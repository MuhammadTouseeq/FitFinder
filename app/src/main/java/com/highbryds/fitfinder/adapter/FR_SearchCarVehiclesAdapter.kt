package com.highbryds.fitfinder.adapter

import android.content.Context
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
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.bestmatch

class FR_SearchCarVehiclesAdapter(
    var frSearchcar: FR_SearchCar, var context: Context, var listtype: Int,
    var generalCallBack: GeneralCallBack
) :
    RecyclerView.Adapter<FR_SearchCarVehiclesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById(R.id.name) as TextView
        val CarnColor = itemView.findViewById(R.id.CarnColor) as TextView
        val leavingTimenLocation = itemView.findViewById(R.id.leavingTimenLocation) as TextView
        val endTimenLocation = itemView.findViewById(R.id.endTimenLocation) as TextView
        val ac = itemView.findViewById(R.id.ac) as TextView
        val price = itemView.findViewById(R.id.price) as TextView
        val seats = itemView.findViewById(R.id.seats) as TextView
        val requestRide = itemView.findViewById(R.id.requestRide) as Button
        val driverRating = itemView.findViewById(R.id.driverRating) as RatingBar
        val source = itemView.findViewById(R.id.source) as TextView
        val user_profile = itemView.findViewById(R.id.user_profile) as ImageView


        fun bindViews(frSearchcar: bestmatch, context: Context) {
//frSearchcar.bestmatch.get(0).user_data.rating
            name.text = frSearchcar.regno
            driverRating.rating = frSearchcar.user_data?.rating!!

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
            seats.text = "${frSearchcar.seatsleft} seats available"

            Glide
                .with(context)
                .load(frSearchcar.user_data.imageUrl)
                .placeholder(R.drawable.user_profile)
                .into(user_profile);

            if (frSearchcar.aC.equals("YES")) {

                seats.text = "${frSearchcar.seatsleft} seats available (with AC)"

            } else {
                seats.text = "${frSearchcar.seatsleft} seats available (without AC)"


            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.fr_search_car_list_item,
            parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindViews(frSearchcar.bestmatch!![position], context)

        holder.requestRide.setOnClickListener {

            generalCallBack.eventOccur(frSearchcar.bestmatch!![position]._id)
        }


    }

    override fun getItemCount(): Int {
        return frSearchcar.bestmatch!!.size

    }

}