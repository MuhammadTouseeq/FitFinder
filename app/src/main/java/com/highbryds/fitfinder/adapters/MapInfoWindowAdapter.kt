package com.highbryds.fitfinder.adapters

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.highbryds.fitfinder.R
import kotlinx.android.synthetic.main.view_info_window.*
 class MyInfoWindowAdapter(context: Activity) : GoogleMap.InfoWindowAdapter {
    private val myContentsView: View

    private val context: Activity

    override fun getInfoWindow(marker: Marker): View {
        var btn_submit = myContentsView.findViewById(R.id.txtItemDesc) as TextView
        btn_submit.text=marker.snippet.toString()
        return myContentsView
    }

    override fun getInfoContents(marker: Marker): View? {
        // TODO Auto-generated method stub
        return null
    }

    init {
        myContentsView = context.layoutInflater.inflate(
            R.layout.view_info_window, null
        )

        this.context = context
    }
}