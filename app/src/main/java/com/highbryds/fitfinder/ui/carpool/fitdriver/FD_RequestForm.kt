package com.highbryds.fitfinder.ui.carpool.fitdriver

import android.R.attr
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.FR_RegistrationLandmarksChipsAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.*
import com.highbryds.fitfinder.model.*
import com.highbryds.fitfinder.model.carpool.FD_CarPool
import com.highbryds.fitfinder.ui.StoryView.StoryFullViewActivity
import com.highbryds.fitfinder.vm.CarPool.FR_SearchCarVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_f_r__request_form.*
import kotlinx.android.synthetic.main.activity_f_r__request_form.RR_progress
import kotlinx.android.synthetic.main.activity_f_r__request_form.RV_landmarks
import kotlinx.android.synthetic.main.activity_f_r__request_form.destination
import kotlinx.android.synthetic.main.activity_f_r__request_form.landmarks
import kotlinx.android.synthetic.main.activity_f_r__request_form.leavingTime
import kotlinx.android.synthetic.main.activity_f_r__request_form.prefFare
import kotlinx.android.synthetic.main.activity_f_r__request_form.startingAddress
import kotlinx.android.synthetic.main.activity_fd_add_carpool.*
import kotlinx.android.synthetic.main.activity_home_map.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


var adapter: FR_RegistrationLandmarksChipsAdapter? = null
var chipModel = ArrayList<FR_RegistrationLandmarksChipsModel>()


@AndroidEntryPoint
class FD_RequestForm : AppCompatActivity(), ApiResponseCallBack , StoryCallback {

    private lateinit var startingPoint: String
    private lateinit var startingPointLatLng: String

    private lateinit var endPoint: String
    private lateinit var endPointLatLng: String

    @Inject
    lateinit var carpoolViewModel: FD_CarpoolViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fd_add_carpool)

        carpoolViewModel.apiResponseCallBack = this

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = "Fit Driver Carpool"
        toolbar.setNavigationOnClickListener {
            finish()
        }

        leavingTime.setOnClickListener {
            setFromTime()
        }

        landmarks.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                JavaHelper.hideKeyboard(this)
                val latlng = JavaHelper.getLocationFromAddress(this , landmarks.text.toString())
                if (latlng != null){
                    chipModel.add(FR_RegistrationLandmarksChipsModel(landmarks.text.toString() , latlng.toString()))
                    adapter?.notifyDataSetChanged()
                    landmarks.setText("")
                }else{
                    this.toast(this , "Not Found, Please use widely known places")
                }
                true
            } else false
        })

        startingAddress.setOnClickListener {
            val intent = Intent(this, FD_Adress_Activity::class.java)
            startActivityForResult(intent, 1);
        }


        destination.setOnClickListener {
            val intent = Intent(this, FD_Adress_Activity::class.java)
            startActivityForResult(intent, 2);
        }

        addtoCarpool.setOnClickListener {

            if (leavingTime.text.toString().isEmpty() || startingAddress.text.toString().isEmpty() || destination.text.toString().isEmpty() || chipModel.size == 0 || prefFare.text.toString().isEmpty()){
                this.toast(this , "Please fill complete details")
            }else{
                RR_progress.visibility = View.VISIBLE
                val destinationLandmarks = ArrayList<FR_SearchCarDestinationLandmarks>()
                for (item in chipModel){
                    item.latlng =  item.latlng.replace("lat/lng: (" , "")
                    item.latlng = item.latlng.replace(")" , "")
                    destinationLandmarks.add(FR_SearchCarDestinationLandmarks(item.landMark, item.latlng.split(",".toRegex())[0].toDoubleOrNull()!! , item.latlng.split(",".toRegex())[1].toDoubleOrNull()!!))
                }

                endPointLatLng =  endPointLatLng.replace("lat/lng: (" , "")
                endPointLatLng =endPointLatLng.replace(")" , "")

                startingPointLatLng =  startingPointLatLng.replace("lat/lng: (" , "")
                startingPointLatLng =startingPointLatLng.replace(")" , "")

                val destinationPoint = FR_SearchCarDestinationPoint(endPoint , endPointLatLng.split(",".toRegex())[0].toDoubleOrNull()!! , endPointLatLng.split(",".toRegex())[1].toDoubleOrNull()!!)
                //val startingPoint = FR_SearchCarStartingPoint(startingPoint, startingPointLatLng.split(",".toRegex())[0].toDoubleOrNull()!! , startingPointLatLng.split(",".toRegex())[1].toDoubleOrNull()!!)
                val startingPoint = FR_SearchCarStartingPoint(startingPoint, startingPointLatLng.split(",".toRegex())[0].toDoubleOrNull()!! , startingPointLatLng.split(",".toRegex())[1].toDoubleOrNull()!!)


                val json = PrefsHelper.getString(Constants.Pref_CarData)
                var model=Gson().fromJson<FD_CarPool>(json, FD_CarPool::class.java)
                model.apply {
                    //startingtime = "2020-10-18" + "T${leavingTime.text.toString()}"
                    startingtime = "${JavaHelper.getDateTime()}" + "T${leavingTime.text.toString()}"
                    destination_landmarks = destinationLandmarks
                    destination=destinationPoint
                    starting_point=startingPoint
                    socialId=KotlinHelper.getUsersData().SocialId
                }

                carpoolViewModel.submitToCarpool(model)

            }



        }

        addCarDetails.setOnClickListener {


            val intent = Intent(this, AddCarActivity::class.java)
            startActivity(intent)
        }

        setChipAdapter()

    }

    fun setFromTime() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            leavingTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun setChipAdapter() {
        RV_landmarks.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = FR_RegistrationLandmarksChipsAdapter(chipModel, this , this)
        RV_landmarks.adapter = adapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode === RESULT_OK) {
                val add = data?.extras?.get("ADDRESS").toString()
                val loc = data?.extras?.get("LATLNG").toString()
                startingAddress.setText(add)
                startingPoint = add
                startingPointLatLng = loc
            }
        } else if (requestCode == 2) {
            if (resultCode === RESULT_OK) {
                val add = data?.extras?.get("ADDRESS").toString()
                val loc = data?.extras?.get("LATLNG").toString()
                destination.setText(add)
                endPoint = add
                endPointLatLng = loc
            }
        }
    }

    override fun getError(error: String) {
        RR_progress.visibility = View.GONE
        this.toast(this , error.toString())
    }

    override fun getSuccess(success: String) {
        RR_progress.visibility = View.GONE
        this.toast(this , success.toString())
        finish()
    }

    override fun storyItemPosition(position: Int) {
        // This is not a story, this is reuse of story callback for landmark chips callback
        chipModel.removeAt(position)
        adapter?.notifyDataSetChanged()

    }
}