package com.highbryds.fitfinder.ui.carpool.fitrider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.JavaHelper
import com.highbryds.fitfinder.commonHelper.MapStyling
import com.highbryds.fitfinder.commonHelper.toast
import kotlinx.android.synthetic.main.activity_f_r__adress_.*


class FR_Adress_Activity : AppCompatActivity(), OnMapReadyCallback,
    LocationSource.OnLocationChangedListener, LocationListener {

    lateinit var currentLocation: LatLng
    private lateinit var mMapGoogleFragment: SupportMapFragment
    private lateinit var mGoogleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_r__adress_)


        IV_done.setOnClickListener {
            if (address.text.toString().isEmpty()){
                this.toast(this, "Address field is empty")
            }else{
                val resultIntent = Intent()
                resultIntent.putExtra("ADDRESS", address.text.toString())
                resultIntent.putExtra("LATLNG", currentLocation)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }


        address.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                JavaHelper.hideKeyboard(this)
                val loc = JavaHelper.getLocationFromAddress(this, address.text.toString().trim())
                if (loc != null) {
                    moveGoogleMap(loc)
                } else {
                    this.toast(this, "Not Found, Please use widely known places")
                }

                //address.setText(JavaHelper.getAddress(this , loc.latitude , loc.longitude))
                true
            } else false
        })


        //bindGoogleFusedLocationClient()
        bindGoogleMap()
    }

    private fun bindGoogleMap() {
        mMapGoogleFragment =
            supportFragmentManager.findFragmentById(com.highbryds.fitfinder.R.id.map) as SupportMapFragment
        mMapGoogleFragment.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap?) {
        mGoogleMap = map!!
        configMap()

        mGoogleMap.setOnCameraIdleListener(GoogleMap.OnCameraIdleListener {
            currentLocation = mGoogleMap.getCameraPosition().target
            Log.d("LOCATION", "${currentLocation.latitude},${currentLocation.longitude}")
            address.setText(
                JavaHelper.getAddress(
                    this,
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            )
        })

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()

            val location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false)!!)
            if (location != null) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 13f
                    )
                )
                val cameraPosition = CameraPosition.Builder()
                    .target(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    ) // Sets the center of the map to location user
                    .zoom(14f) // Sets the zoom
                    .bearing(50f) // Sets the orientation of the camera to east
                    .tilt(20f) // Sets the tilt of the camera to 30 degrees
                    .build() // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

        }

    }

    private fun configMap() {
        mGoogleMap.setMapStyle(MapStyling.styleMap())
        mGoogleMap.isMyLocationEnabled = true
        mGoogleMap.uiSettings.isMyLocationButtonEnabled = true

        val locationButton= (mMapGoogleFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,0,200);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.isMyLocationEnabled = true
        }
    }

    override fun onLocationChanged(p0: Location?) {
        Log.d("loca", "${p0?.latitude} , ${p0?.longitude}")
    }


    private fun moveGoogleMap(latLng: LatLng) {

        currentLocation = latLng
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }


}