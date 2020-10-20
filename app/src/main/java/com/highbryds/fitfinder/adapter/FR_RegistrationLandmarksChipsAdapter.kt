package com.highbryds.fitfinder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.model.FR_RegistrationLandmarksChipsModel

class FR_RegistrationLandmarksChipsAdapter(var landmark: ArrayList<FR_RegistrationLandmarksChipsModel>, var context: Context,
var storyCallback: StoryCallback) :
    RecyclerView.Adapter<FR_RegistrationLandmarksChipsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chips = itemView.findViewById(R.id.chipitem) as TextView
        val CV_deleteLandmark = itemView.findViewById(R.id.CV_deleteLandmark) as CardView


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

        holder.CV_deleteLandmark.setOnClickListener {
            storyCallback.storyItemPosition(position)
        }
    }

    override fun getItemCount(): Int {
        return landmark.size
    }


}