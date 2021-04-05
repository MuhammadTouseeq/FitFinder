package com.highbryds.fitfinder.ui.carpool.fitrider

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.FR_PendingRequestAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.GeneralCallBack
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.carpool.RIDE_STATUS
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.model.carpool.RideStatus
import com.highbryds.fitfinder.ui.carpool.fitdriver.FD_CarpoolViewModel
import com.highbryds.fitfinder.vm.CarPool.FR_SearchCarVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_f_r__pending_request.*
import javax.inject.Inject


@AndroidEntryPoint
class FR_PendingRequest : AppCompatActivity(), ApiResponseCallBack, GeneralCallBack {

    @Inject
    lateinit var fr_SearchCarVM: FR_SearchCarVM

    @Inject
    lateinit var FD_CarpoolViewModel: FD_CarpoolViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_r__pending_request)

        fr_SearchCarVM.apiResponseCallBack = this
        FD_CarpoolViewModel.apiResponseCallBack = this


        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Pending Requests"
        toolbar.setNavigationOnClickListener { finish() }

        RV_pending.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val rideRequest = RideRequest(null, KotlinHelper.getUsersData().SocialId, null, null)
        fr_SearchCarVM.getPendingRequest(rideRequest)
        fr_SearchCarVM.pendingRequest.observe(this, Observer {
            // TODO: 03/04/2021 to check below condition of empty carpool data

            if (it.carpooldata != null && !it.carpooldata.isEmpty()) {
                Log.d("PendingRequest", it.toString())
                val adapter = FR_PendingRequestAdapter(it, this, 0, this)
                RV_pending.adapter = adapter
            } else {
                val intent = Intent(this, FR_RequestForm::class.java)
                startActivity(intent)
                finish()
            }
            progress.visibility = View.GONE

        })

        FD_CarpoolViewModel.isRideStatusChange.observe(this, Observer {
            finish()
        })
    }

    override fun getError(error: String) {
        this.toast(this, error.toString())
    }

    override fun getSuccess(success: String) {
        this.toast(this, success.toString())
    }

    override fun eventOccur(carpoolstatus_id: String) {
        val model = RideStatus(RIDE_STATUS.cancelled.name, carpoolstatus_id, null, null)
        FD_CarpoolViewModel.changeRideStatus(model)
    }

    override fun eventOccurCancelRideRating(carpoolstatus_id: String, socialID: String) {
        showDialog(carpoolstatus_id, socialID)
    }


    fun showDialog(carpoolstatus_id: String, socialID: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.ratindialoge)

        val submit = dialog.findViewById(R.id.submit) as Button
        val ratingBar = dialog.findViewById(R.id.ratingBar) as RatingBar

        submit.setOnClickListener {
            val model = RideStatus(
                RIDE_STATUS.completed.name,
                carpoolstatus_id,
                ratingBar.rating.toDouble(),
                socialID
            )
            FD_CarpoolViewModel.changeRideStatus(model)
            dialog.dismiss()
        }

        dialog.show()
    }
}