package com.highbryds.fitfinder.ui.carpool.fitrider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.FR_SearchCarVehiclesAdapter
import com.highbryds.fitfinder.adapter.FR_SearchCarVehiclesAdapter_Other
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.GeneralCallBack
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.model.othermatch
import com.highbryds.fitfinder.vm.CarPool.FR_SearchCarVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_f_r__search_car_list.*
import javax.inject.Inject

@AndroidEntryPoint
class FR_SearchCarList : AppCompatActivity(), GeneralCallBack, ApiResponseCallBack {

    lateinit var frSearchcar: FR_SearchCar
    lateinit var frSearchcarForm: FR_SearchCar

    @Inject
    lateinit var frSearchcarvm: FR_SearchCarVM
    lateinit var id: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_r__search_car_list)


        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Fit Driver"
        toolbar.setNavigationOnClickListener { finish() }


        frSearchcarvm.apiResponseCallBack = this
        RV_recommended.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        RV_others.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        var json = intent?.getStringExtra("SearchCar")
        frSearchcar = Gson().fromJson(json, FR_SearchCar::class.java)



        json = intent?.getStringExtra("SearchCarForm")
        frSearchcarForm = Gson().fromJson(json, FR_SearchCar::class.java)


        if (frSearchcar.bestmatch!!.size > 0) {
            noRecommended.visibility = View.GONE
            val adapter = FR_SearchCarVehiclesAdapter(frSearchcar, this, 0, this)
            RV_recommended.adapter = adapter
        } else {
            noRecommended.visibility = View.VISIBLE
        }

        if (frSearchcar.others!!.size > 0) {
            noOther.visibility = View.GONE
            val adapter = FR_SearchCarVehiclesAdapter_Other(frSearchcar, this, 1, this)
            RV_others.adapter = adapter
        } else {
            noOther.visibility = View.VISIBLE
        }
    }

    override fun eventOccur(id: String) {
        val rideRequest =
            RideRequest(
                id,
                KotlinHelper.getUsersData().SocialId,
                KotlinHelper.getUsersData().name,
                frSearchcarForm
            )
        this.id = id
        frSearchcarvm.sendRequest(rideRequest)
    }

    override fun eventOccurCancelRideRating(carpoolstatus_id: String, socialID: String) {

    }


    override fun getError(error: String) {
        this.toast(this, error.toString())
    }

    override fun getSuccess(success: String) {
        //if (success.contains("pending" , true)) {
        val intent = Intent(this, FR_PendingRequest::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
        // }
        this.toast(this, success.toString())
    }
}