package com.highbryds.fitfinder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.GeneralCallBack
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.bestmatch
import com.highbryds.fitfinder.model.othermatch

class FR_SearchCarVehiclesAdapter_Other(
    var frSearchcar: FR_SearchCar, var context: Context, var listtype: Int,
    var generalCallBack: GeneralCallBack
) :
    RecyclerView.Adapter<FR_SearchCarVehiclesAdapter_Other.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById(R.id.name) as TextView
        val CarnColor = itemView.findViewById(R.id.CarnColor) as TextView
        val leavingTimenLocation = itemView.findViewById(R.id.leavingTimenLocation) as TextView
        val endTimenLocation = itemView.findViewById(R.id.endTimenLocation) as TextView
        val ac = itemView.findViewById(R.id.ac) as TextView
        val price = itemView.findViewById(R.id.price) as TextView
        val seats = itemView.findViewById(R.id.seats) as TextView
        val requestRide = itemView.findViewById(R.id.requestRide) as Button

        fun bindViews(frSearchcar: othermatch) {

            name.text = frSearchcar.regno
            CarnColor.text = "${frSearchcar.carmake} ${frSearchcar.carmodel} | ${frSearchcar.color}"
            leavingTimenLocation.text = "At: ${JavaHelper.parseDateToFormat("yyyy-MM-ddTHH:mm", frSearchcar.startingtime)
            } From: ${frSearchcar.starting_point.name}"
            endTimenLocation.text = "To: ${frSearchcar.destination.name}"
            ac.text = "AC:\n${frSearchcar.aC}"
            price.text = "Rs:\n${frSearchcar.preferredcost_min}"
            seats.text = "Seats:\n${frSearchcar.seatsleft}/${frSearchcar.totalseats}"

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

        holder.bindViews(frSearchcar.others!![position])

        holder.requestRide.setOnClickListener {
            generalCallBack.eventOccur(frSearchcar.others!![position]._id)
        }
    }

    override fun getItemCount(): Int {
        if (listtype == 0) {
            return frSearchcar.bestmatch!!.size
        } else {
            return frSearchcar.others!!.size
        }

    }


}