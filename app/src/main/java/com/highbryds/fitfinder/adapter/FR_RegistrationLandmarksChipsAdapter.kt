package com.highbryds.fitfinder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.model.FR_RegistrationLandmarksChipsModel

class FR_RegistrationLandmarksChipsAdapter(var landmark: ArrayList<FR_RegistrationLandmarksChipsModel>, var context: Context) :
    RecyclerView.Adapter<FR_RegistrationLandmarksChipsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chips = itemView.findViewById(R.id.chipitem) as TextView


        fun bindViews(landmark: FR_RegistrationLandmarksChipsModel, context: Context) {

            chips.text = landmark.landMark

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chip_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(landmark[position], context)

    }

    override fun getItemCount(): Int {
        return landmark.size
    }


}