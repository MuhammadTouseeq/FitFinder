package com.highbryds.fitfinder.ui.carpool.fitdriver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.RideRequestsAdapter
import com.highbryds.fitfinder.adapters.StoryCommentsAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.RideActionCallback
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.carpool.RIDE_STATUS
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.model.carpool.RideStatus
import com.highbryds.fitfinder.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.general_recycle_view.*
import javax.inject.Inject

@AndroidEntryPoint
class RideRequestsActivity : BaseActivity(), ApiResponseCallBack {

    private lateinit var adapter: RideRequestsAdapter

    @Inject
    lateinit var FD_CarpoolViewModel: FD_CarpoolViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_recycle_view)


        //bindToolbar(toolbar,"My Request")
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = "My Request"

        adapter = RideRequestsAdapter(applicationContext, arrayListOf())
        recycler_view.adapter = adapter


        adapter.rideActionCallback = object : RideActionCallback {

            override fun onItemClicked(position: Int, view: View) {
                RR_progress.visibility = View.VISIBLE

                val entity = adapter.getitem(position)
                when (view.id) {
                    R.id.btnCancel -> {
                        val model = RideStatus(RIDE_STATUS.cancelled.name, entity._id!! , null , null)
                        FD_CarpoolViewModel.changeRideStatus(model)
                    }
                    R.id.btnRequestStatus -> {
                        val model = RideStatus(RIDE_STATUS.accepted.name, entity._id!! , null , null)
                        FD_CarpoolViewModel.changeRideStatus(model)
                    }
                }
            }

        }

        //val arrData= mutableListOf<RideRequest>()
        //   for (i in 0..9){ arrData.add(RideRequest()) }
//        adapter.addData(arrData)

        FD_CarpoolViewModel.apiResponseCallBack = this

        //FD_CarpoolViewModel.getRiderRequest("115362360601650573089")
        FD_CarpoolViewModel.getRiderRequest(KotlinHelper.getUsersData().SocialId)

        RR_progress.visibility = View.VISIBLE


        FD_CarpoolViewModel.isRideStatusChange.observe(this, Observer {

            if (it) {
                adapter.clearData()
                FD_CarpoolViewModel.getRiderRequest(KotlinHelper.getUsersData().SocialId)
            }
        })

        FD_CarpoolViewModel.riderRequestData.observe(this, Observer {

            it?.let {

                RR_progress.visibility = View.GONE
                if (it.size == 0) {
                    emptyView.visibility = View.VISIBLE
                    recycler_view.visibility = View.GONE
                } else {

                    emptyView.visibility = View.GONE
                    recycler_view.visibility = View.VISIBLE
                    adapter.clearData()
                    adapter.addData(it)
                    adapter.notifyDataSetChanged()
                }
            }


        })

        btnAddToCarpool.setOnClickListener {

            val intent = Intent(this, FD_RequestForm::class.java)
            startActivity(intent)

        }

    }

    override fun getError(error: String) {
        RR_progress.visibility = View.GONE
        this.toast(this, error.toString())
    }

    override fun getSuccess(success: String) {


        FD_CarpoolViewModel.getRiderRequest(KotlinHelper.getUsersData().SocialId)

        RR_progress.visibility = View.GONE
        this.toast(this, success.toString())
    }


}