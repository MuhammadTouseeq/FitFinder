package com.highbryds.fitfinder.ui.carpool.fitdriver

import android.content.Intent
import android.os.Bundle
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.RideRequestsAdapter
import com.highbryds.fitfinder.adapters.StoryCommentsAdapter
import com.highbryds.fitfinder.model.carpool.RideRequest
import com.highbryds.fitfinder.ui.BaseActivity
import kotlinx.android.synthetic.main.general_recycle_view.*
import kotlinx.android.synthetic.main.view_toolbar.*


class RideRequestsActivity: BaseActivity() {

    private lateinit var adapter: RideRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.general_recycle_view)


        bindToolbar(toolbar,"My Request")

        adapter = RideRequestsAdapter(applicationContext, arrayListOf())
        recycler_view.adapter=adapter

        val arrData= mutableListOf<RideRequest>()
        //for (i in 0..9){ arrData.add(RideRequest()) }

        adapter.addData(arrData)

        btnAddToCarpool.setOnClickListener {

            val intent = Intent(this, FD_RequestForm::class.java)
            startActivity(intent)

        }

    }
}