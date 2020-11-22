package com.highbryds.fitfinder.adapters

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.RideActionCallback
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.model.carpool.RIDE_STATUS
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.ui.Chatting.context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.log4k.d
import kotlinx.android.synthetic.main.recycler_item_story_trending.view.txtUserName
import kotlinx.android.synthetic.main.rv_item_ride_requests.view.*
import kotlinx.android.synthetic.main.rv_item_story_comment.view.*
import kotlinx.android.synthetic.main.rv_item_story_comment.view.delete_icon
import kotlinx.android.synthetic.main.rv_item_story_comment.view.imgProfile
import kotlinx.android.synthetic.main.rv_item_story_comment.view.txtDateTime


class RideRequestsAdapter(
    val mcontext: Context,
    private val arrData: ArrayList<RideRequest>
) : RecyclerView.Adapter<RideRequestsAdapter.DataViewHolder>() {


    lateinit var rideActionCallback: RideActionCallback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_item_ride_requests, parent,
                false
            )
        )

    override fun getItemCount(): Int = arrData.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(arrData[position], mcontext)

        holder.btnCancel.setOnClickListener {

            rideActionCallback?.onItemClicked(position,it)
        }

        if(arrData.get(position).status.equals("pending"))
        {
            holder.btnRequestStatus.setOnClickListener {

                rideActionCallback?.onItemClicked(position,it)

            }
        }
        else
        {
            holder.btnRequestStatus.setOnClickListener(null)
        }
    }

    fun getitem(position: Int): RideRequest {
        return arrData?.get(position)

    }

    fun addData(list: List<RideRequest>) {
        arrData.addAll(list)
    }

    fun clearData() {
        if (arrData?.size > 0) arrData.clear()
        notifyDataSetChanged()
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtUserName = itemView.txtUserName
        val imgProfile = itemView.imgProfile
        val txtDateTime = itemView.txtDateTime
        val txtDesignation = itemView.txtDesignation
        val txtDestination = itemView.txtDestination
        val txtStartingPoint = itemView.txtStartingPoint
        val delete_icon = itemView.delete_icon
        val btnRequestStatus = itemView.btnRequestStatus
        val btnCancel = itemView.btnCancel
        val btnCall = itemView.btnCall

        fun bind(
            model: RideRequest,
            mcontext: Context
        ) {
            with(model)
            {
                // txtDateTime.setText(Utils.getDateTimeFromServer(createdAt,"EEE, d MMM yyyy h:mm a"))

                txtDestination.text="To: ${destination?.name}"
                //txtDestination.isSelected=true
               // txtStartingPoint.isSelected=true
                txtStartingPoint.text="Form :${starting_point?.name}"
                txtDateTime.text="Request Time : ${JavaHelper.parseDateToFormat(null,startingtime)}"

                when(status)
                {
                    RIDE_STATUS.pending.name->{
                btnRequestStatus.setText("Accept")
                        btnCancel.visibility=View.VISIBLE

                    }
                    RIDE_STATUS.cancelled.name->{
                        btnRequestStatus.setText("Cancelled")
                        btnCancel.visibility=View.INVISIBLE

                    }
                    RIDE_STATUS.accepted.name->{
                        btnRequestStatus.setText("Accepted")
btnCancel.visibility=View.INVISIBLE
                    }
                }

                txtUserName.text = usersData?.name
                txtDesignation.text = usersData?.About


                Glide
                    .with(mcontext)
                    .load(usersData?.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .circleCrop()
                    .into(imgProfile);

                btnCall.setOnClickListener {

                        Dexter.withContext(mcontext)
                            .withPermission(
                                android.Manifest.permission.CALL_PHONE
                            ).withListener(object : PermissionListener{
                                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                                    val intent =
                                        Intent(Intent.ACTION_CALL, Uri.parse("tel:" +usersData?.cellNumber))
                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    mcontext.startActivity(intent)

                                }

                                override fun onPermissionRationaleShouldBeShown(
                                    p0: PermissionRequest?,
                                    p1: PermissionToken?
                                ) {

                                }

                                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                                }

                            }).check()




                }

            }

        }
    }

}