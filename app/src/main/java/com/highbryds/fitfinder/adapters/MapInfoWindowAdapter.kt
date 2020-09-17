package com.highbryds.fitfinder.adapters

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.utils.Utils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyInfoWindowAdapter(context: Activity) : GoogleMap.InfoWindowAdapter {
    private val myContentsView: View

    private val context: Activity

    override fun getInfoWindow(marker: Marker): View {
        var txtDescription = myContentsView.findViewById(R.id.txtItemDesc) as TextView
        var txtDateTime = myContentsView.findViewById(R.id.txtDateTime) as TextView
        var clapCount = myContentsView.findViewById(R.id.clapCount) as TextView
        var imgProfile = myContentsView.findViewById(R.id.imgProfile) as ImageView
        var imgMediaType = myContentsView.findViewById(R.id.imgMediaType) as ImageView

        marker.snippet?.let {
            val storyData = Gson().fromJson(marker.snippet, NearbyStory::class.java)

            with(storyData)
            {

                txtDateTime.setText(Utils.getDateTimeFromServer(createdAt,"EEE, d MMM yyyy h:mm a"))

                if(mediaUrl.contains(".mp3"))
                {
                    imgMediaType.setImageDrawable(context.resources.getDrawable(R.drawable.icon_audio))
                }
                else if(mediaUrl.contains(".mp4"))
                {
                    imgMediaType.setImageDrawable(context.resources.getDrawable(R.drawable.icon_video))
                }
                else{
                    imgMediaType.setImageDrawable(context.resources.getDrawable(R.drawable.icon_image))
                }

                txtDescription.text = storyName
                if (userData?.size > 0) let {
                    Glide
                        .with(context)
                        .load(userData?.get(0)?.imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .circleCrop()
                        .into(imgProfile);

                } else {
                    imgProfile.setImageDrawable(context.resources.getDrawable(R.drawable
                        .man_cartoon))
                }
                if (storyClapData?.size > 0) let {
                    clapCount.text = storyClapData?.size.toString()
                } else {clapCount.text = ""}

            }

        }
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