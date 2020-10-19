package com.highbryds.fitfinder.ui.carpool.fitrider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.model.FR_SearchCar
import com.highbryds.fitfinder.model.NearbyStory

class FR_SearchCarList : AppCompatActivity() {

    lateinit var frSearchcar: FR_SearchCar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_r__search_car_list)


        val json = intent?.getStringExtra("SearchCar")
        frSearchcar = Gson().fromJson(json, FR_SearchCar::class.java)
        frSearchcar
    }
}