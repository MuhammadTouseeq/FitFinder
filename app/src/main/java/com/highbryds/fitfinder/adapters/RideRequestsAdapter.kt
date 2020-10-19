package com.highbryds.fitfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.utils.Utils
import kotlinx.android.synthetic.main.recycler_item_story_trending.view.txtUserName
import kotlinx.android.synthetic.main.rv_item_story_comment.view.*


class RideRequestsAdapter(val mcontext: Context,
                          private val arrData: ArrayList<RideRequest>
) : RecyclerView.Adapter<RideRequestsAdapter.DataViewHolder>() {


    lateinit var storyCallback: StoryCallback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_item_ride_requests, parent,
                false
            )
        )

    override fun getItemCount(): Int = arrData.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int)

    {
        holder.bind(arrData[position],mcontext)

    }

    fun getitem(position:Int): RideRequest {
        return arrData?.get(position)

    }
    fun addData(list: List<RideRequest>) {
        arrData.addAll(list)
    }

    fun clearData()
    {
        if(arrData?.size>0) arrData.clear()
        notifyDataSetChanged()
    }
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtUserName=itemView.txtUserName
        val imgProfile=itemView.imgProfile
        val txtDateTime=itemView.txtDateTime
        val delete_icon=itemView.delete_icon

        fun bind(
            model: RideRequest,
            mcontext: Context
        ) {
            with(model)
            {
               // txtDateTime.setText(Utils.getDateTimeFromServer(createdAt,"EEE, d MMM yyyy h:mm a"))

                txtUserName.text="Andy John"

                    Glide
                        .with(mcontext)
                        .load(R.drawable.user_profile)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .circleCrop()
                        .into(imgProfile);


            }

        }
    }

}