package com.highbryds.fitfinder.ui.carpool.fitrider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.FR_SearchCarVehiclesAdapter
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.othermatch
import kotlinx.android.synthetic.main.activity_f_r__search_car_list.*

class FR_SearchCarList : AppCompatActivity() {

    lateinit var frSearchcar: FR_SearchCar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_r__search_car_list)

        RV_recommended.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        RV_others.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val json = intent?.getStringExtra("SearchCar")
        frSearchcar = Gson().fromJson(json, FR_SearchCar::class.java)


        if (frSearchcar.bestmatch!!.size > 0) {
            noRecommended.visibility = View.GONE
            val adapter = FR_SearchCarVehiclesAdapter(frSearchcar, this, 0)
            RV_recommended.adapter = adapter
        }
        else {
            noRecommended.visibility = View.VISIBLE
        }

        if (frSearchcar.others!!.size > 0) {
            noOther.visibility = View.GONE
            val adapter = FR_SearchCarVehiclesAdapter(frSearchcar, this, 1)
            RV_others.adapter = adapter
        } else {
            noOther.visibility = View.VISIBLE
        }


    }
}