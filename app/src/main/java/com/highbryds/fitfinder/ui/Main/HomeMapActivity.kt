package com.highbryds.fitfinder.ui.Main


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.highbryds.fitfinder.BuildConfig
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.MyInfoWindowAdapter
import com.highbryds.fitfinder.adapters.TrendingStoriesAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.FTPCallback
import com.highbryds.fitfinder.callbacks.videoCompressionCallback
import com.highbryds.fitfinder.commonHelper.*
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.TrendingStory
import com.highbryds.fitfinder.model.UserStory
import com.highbryds.fitfinder.ui.Auth.LoginActivity
import com.highbryds.fitfinder.ui.BaseActivity
import com.highbryds.fitfinder.ui.Profile.UserProfileMain
import com.highbryds.fitfinder.ui.Profile.UserProfileSetting
import com.highbryds.fitfinder.ui.Profile.UserStories
import com.highbryds.fitfinder.vm.AuthViewModels.LogoutViewModel
import com.highbryds.snapryde.rider_app.recievers.GpsLocationReceiver
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.log4k.d
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withEmail
import com.mikepenz.materialdrawer.model.interfaces.withIdentifier
import com.mikepenz.materialdrawer.model.interfaces.withName
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.pakdev.easypicker.utils.EasyImagePicker
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home_map.*
import kotlinx.android.synthetic.main.record_audio_activity.chronometer
import kotlinx.android.synthetic.main.record_audio_activity.imgBtRecord
import kotlinx.android.synthetic.main.record_audio_activity.imgBtStop
import kotlinx.android.synthetic.main.record_audio_activity.imgViewPlay
import kotlinx.android.synthetic.main.record_audio_activity.llPlay
import kotlinx.android.synthetic.main.record_audio_activity.llRecorder
import kotlinx.android.synthetic.main.record_audio_activity.seekBar
import kotlinx.android.synthetic.main.view_audio_recorder.*
import kotlinx.android.synthetic.main.view_bottom_sheet.*
import kotlinx.android.synthetic.main.view_turn_location_on.*
import kotlinx.android.synthetic.main.view_video_recorder.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
open class HomeMapActivity : BaseActivity(), OnMapReadyCallback, View.OnClickListener,
    ApiResponseCallBack, videoCompressionCallback, FTPCallback {


    private val TAG = HomeMapActivity::class.java!!.getSimpleName()

    private val UPDATE_INTERVAL: Long = 5000
    private val REQUEST_SETTINGS: Int = 0x2
    private val FASTEST_INTERVAL: Long = 5000; // = 5 seconds

    private lateinit var LocationRequest: LocationRequest

    private lateinit var mMapGoogleFragment: SupportMapFragment
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    lateinit var reciever: GpsLocationReceiver

    lateinit var currentMarker: Marker
    lateinit var currentLocation: LatLng

    //=======Audio Recorder Variables
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    var fileName: String? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private val RECORD_AUDIO_REQUEST_CODE = 101
    private var isPlaying = false

    //========End=====//
    private val SELECT_VIDEO = 1
    private val ACTION_TAKE_VIDEO = 122

    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var homeMapViewModel: HomeMapViewModel

    @Inject
    lateinit var logoutViewModel: LogoutViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_map)

        //    bindNavigationDrawer(toolbar)

        homeMapViewModel.apiErrorsCallBack = this
        logoutViewModel.apiResponseCallBack = this

        bnidBottomSheet()

        bindGoogleFusedLocationClient()

        bindGoogleMap()

        hideShowView()

        setupTrendingStories()

        btnLocationTurnon.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestLocationPermissions() else showAutoGPSDialog()
        }

        btnAudioRecorder.setOnClickListener(this)
        btnCamera.setOnClickListener(this)
        btnGallery.setOnClickListener(this)
        btnVideo.setOnClickListener(this)
        btnSend.setOnClickListener(this)

        //=======Audio Recorder Listeners
        imgBtRecord.setOnClickListener(this)
        imgBtStop.setOnClickListener(this)
        imgViewPlay.setOnClickListener(this)




        homeMapViewModel.observeAllNearByStories().observe(this, androidx.lifecycle.Observer {

            for (item: NearbyStory in it) {
                Log.d("StoryData", item.mediaUrl)
                if (item.latitude != 0.0) {
                    //   mGoogleMap.clear()
                    addStoryMarker(this, item)
                }

            }
        })

        drawerSetup()
        IV_Slider.setOnClickListener {
            slider.drawerLayout?.openDrawer(slider)
        }

    }

    fun drawerSetup() {

        // Create the AccountHeader
        val headerView = AccountHeaderView(this).apply {
            attachToSliderView(slider) // attach to the slider
            addProfiles(
                ProfileDrawerItem().withName(KotlinHelper.getUsersData().name).withEmail(
                    KotlinHelper.getUsersData().emailAdd
                )
            )
            onAccountHeaderListener = { view, profile, current ->
                // react to profile changes
                false
            }
        }


        val imageView = headerView.currentProfileView
        Glide
            .with(this)
            .load(KotlinHelper.getUsersData().imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(imageView);

        //if you want to update the items at a later time it is recommended to keep it in a variable
        val home = PrimaryDrawerItem().withIdentifier(1).withName("Home")
        val story = PrimaryDrawerItem().withIdentifier(2).withName("My Story")
        val profile = PrimaryDrawerItem().withIdentifier(3).withName("Profile")
        val settings = SecondaryDrawerItem().withIdentifier(5).withName("Settings")
        val logout = SecondaryDrawerItem().withIdentifier(4).withName("Logout")


        // get the reference to the slider and add the items
        slider.itemAdapter.add(
            home, profile, story,
            DividerDrawerItem(),
            settings,logout
        )

        slider.headerView = headerView

        // specify a click listener
        slider.onDrawerItemClickListener = { v, drawerItem, position ->
            when (position) {
                1 -> {
                    this.toast(this, "Home")
                }
                2 -> {
                    val intent = Intent(this, UserProfileMain::class.java)
                    startActivity(intent)
                }
                3 -> {
                    val intent = Intent(this, UserStories::class.java)
                    startActivity(intent)
                }
                5 -> {
                    val intent = Intent(this, UserProfileSetting::class.java)
                    startActivity(intent)
                }
                6 -> {

                    logoutViewModel.logoutUser(KotlinHelper.getUsersData().SocialId)
                    PrefsHelper.putBoolean(Constants.Pref_IsLogin , false)
                }
            }
            false
        }

    }


/* fun buildNavigationDrawer() {


        // Create the AccountHeader
        val headerResult: AccountHeader = AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.color.colorPrimary)
            .addProfiles(
                ProfileDrawerItem().withName("Muhammad Touseeq").withEmail("touseeq@gmail.com")
                    .withTextColor(resources.getColor(R.color.colorPrimary))
            )
            .withOnAccountHeaderListener(object : OnAccountHeaderListener() {
                fun onProfileChanged(
                    view: View?,
                    profile: IProfile?,
                    current: Boolean
                ): Boolean {

                    //addDockableFragment(ProfileFragment.newInstance());
                    return false
                }
            })
            .build()
        //create the drawer and remember the `Drawer` result object
        result = DrawerBuilder()
            .withActivity(this)
            .withAccountHeader(headerResult) //                .withToolbar(toolbar)
            .addDrawerItems(
                PrimaryDrawerItem().withName("Home").withIdentifier(1),
                PrimaryDrawerItem().withName("My Stories").withIdentifier(2),
                PrimaryDrawerItem().withName("Contact Us").withIdentifier(3),
                PrimaryDrawerItem().withName("Settings").withIdentifier(4),
                PrimaryDrawerItem().withName("About App")
                    .withIdentifier(5) // expandableItemOrderNow
                //                        new DividerDrawerItem(),
            ).withOnDrawerItemClickListener(object : OnDrawerItemClickListener() {
                fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem
                ): Boolean {
                    val itemName: String = (drawerItem as Nameable).getName().toString()
                    when (position) {
                        1 -> {
                            result.closeDrawer()
                        }
                        2 -> {
                            result.closeDrawer()
                        }
                        4 -> {
                            result.closeDrawer()
                        }
                    }
                    return true
                }
            }).build()
    }*/

    open fun addStoryMarker(
        context: Context,
        story: NearbyStory
    ): Bitmap? {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                com.highbryds.fitfinder.R.layout.marker_view,
                null
            )
        val markerImage: ImageView =
            marker.findViewById<View>(com.highbryds.fitfinder.R.id.imgProfile) as CircleImageView
        markerImage.setImageDrawable(resources.getDrawable(com.highbryds.fitfinder.R.drawable.marker_thinking_boy))
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)

        val pinMarker: Marker = mGoogleMap.addMarker(
            MarkerOptions()
                .title("New Message")
                .snippet(story.storyName + "")
                .visible(true)
                .position(LatLng(story.latitude.toDouble(), story.longitude.toDouble()))
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        )


        return bitmap
    }

    open fun createCustomMarker(
        context: Context,
        latLng: LatLng
    ): Bitmap? {
        val marker: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                com.highbryds.fitfinder.R.layout.marker_view,
                null
            )
        val markerImage: ImageView =
            marker.findViewById<View>(com.highbryds.fitfinder.R.id.imgProfile) as CircleImageView
        markerImage.setImageDrawable(resources.getDrawable(com.highbryds.fitfinder.R.drawable.marker_thinking_boy))
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT)
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)

        val pinMarker: Marker = mGoogleMap.addMarker(
            MarkerOptions()
                .title("New Message")
                .snippet("Too much traffic jam in Karachi")
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        )

//        Picasso.get().load(filePath).fit().centerCrop().into(markerImage,object :Callback{
//            override fun onSuccess() {
//                val pinMarker: Marker = mGoogeMap.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                )
//            }
//
//            override fun onError(e: Exception?) {
//               e!!.printStackTrace()
//            }
//
//        } )


        return bitmap
    }

    //Check the GPS then hide or show view
    private fun hideShowView() {
        if (AppUtils.isGpsEnabled(this)) {
            view.visibility = View.GONE
            cardTurnOnLocation.visibility = View.GONE

            startLocationUpdates()
        } else {
            view.visibility = View.VISIBLE
            cardTurnOnLocation.visibility = View.VISIBLE
        }
    }


    //Register broadcast reciever for location
    private fun registerLocationBroadcast() {

        val filter: IntentFilter = IntentFilter()
        filter.addAction("android.location.PROVIDERS_CHANGED");
        reciever = GpsLocationReceiver()
        registerReceiver(reciever, filter)

    }

    private fun bindGoogleMap() {
        mMapGoogleFragment =
            supportFragmentManager.findFragmentById(com.highbryds.fitfinder.R.id.map) as SupportMapFragment
        mMapGoogleFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        mGoogleMap = map!!
        configMap()


        homeMapViewModel.userLocation.observe(this, androidx.lifecycle.Observer {

            it?.let {
                homeMapViewModel.fetchNearByStoriesData(
                    it.latitude.toString(),
                    it.longitude.toString()
                )

            }//Requesting for nearByStoies

        })


//                googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
//                    @Override
//                    public void onCameraMoveCanceled() {
//                        Toast.makeText(context, "Camera move cancelled", Toast.LENGTH_SHORT).show();
//                    }
//                });

//                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
//                    @Override
//                    public void onCameraMove() {
//                        Toast.makeText(context, "Camera Move", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
        mGoogleMap.setOnCameraIdleListener(OnCameraIdleListener {
            currentLocation = mGoogleMap.getCameraPosition().target
        })

        mGoogleMap.setInfoWindowAdapter(MyInfoWindowAdapter(this))
//        createCustomMarker(
//            this,
//            LatLng(24.9132197, 67.0671513)
//        );
//        createCustomMarker(
//            this,
//            LatLng(24.8725907, 67.0244649)
//        );
//        createCustomMarker(
//            this,
//            LatLng(24.8728378, 67.0236955)
//        );
        mGoogleMap.setOnMapClickListener {

            bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }


    }

    private fun configMap() {
        mGoogleMap.setMapStyle(MapStyling.styleMap())
        mGoogleMap.uiSettings.isMyLocationButtonEnabled = true

    }

    lateinit var adapter: TrendingStoriesAdapter
    private fun setupTrendingStories() {
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapter = TrendingStoriesAdapter(arrayListOf())
//        recycler_view.addItemDecoration(
//            DividerItemDecoration(
//                recycler_view.context,
//                (recycler_view.layoutManager as LinearLayoutManager).orientation
//            )
//        )
        recycler_view.adapter = adapter


        var items = ArrayList<TrendingStory>()
        for (i in 1..7) {
            items.add(TrendingStory())
        }
        adapter.addData(items)
    }

    //request permission for location
    private fun requestLocationPermissions() {

        Dexter.withActivity(this@HomeMapActivity)
            .withPermissions(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {

                        if (report.areAllPermissionsGranted()) {

                            startLocationUpdates()
                            d("All permisssion granted")
                            showAutoGPSDialog()


                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {

                }

            }).check()
    }


    //binding Fused location provider client and location request for getting location from GPS
    private fun bindGoogleFusedLocationClient() {

        bindLocationRequest()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }


    override fun onResume() {
        super.onResume()
        registerLocationBroadcast()
    }

    override fun onPause() {
        super.onPause()
//Remove location updates
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback!!)
        }

        if (reciever != null) {
            unregisterReceiver(reciever)
        }
    }

    //location callback for fusedLocation provider client
//location updates
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            val lastLocation: Location = locationResult!!.getLastLocation()

            currentLocation = LatLng(lastLocation.latitude, lastLocation.longitude)

            homeMapViewModel.userLocation.value = currentLocation

            stopLocationUpdate()
            moveGoogleMap(currentLocation)

        }
    }


    //Add marker on location get from fused location api
    private fun moveGoogleMap(latLng: LatLng) {

        //mGoogeMap.clear()
        // lat/lng: (24.9132197,67.0671513)

//        currentMarker = mGoogeMap.addMarker(createMarkerFromBitmap(latLng))
//        currentMarker = mGoogeMap.addMarker(MarkerOptions().title("Location").position(latLng))
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }


    fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        homeMapViewModel.userLocation.value = null
    }

    //getting last location and
    private fun startLocationUpdates() {

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {


            view.visibility = View.GONE
            cardTurnOnLocation.visibility = View.GONE

            if (it != null) {
                moveGoogleMap(LatLng(it.latitude, it.longitude))
            }
        }.addOnFailureListener {

            view.visibility = View.VISIBLE
            cardTurnOnLocation.visibility = View.VISIBLE
        }



        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest,
            locationCallback,
            Looper.getMainLooper()
        )


    }

    //bottom sheet binding
    private fun bnidBottomSheet() {

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout)

        coordinatorLayout.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.getState() === BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        })


        bottom_sheet_layout.setOnClickListener {
            toggleBottomSheet(bottomSheetBehavior)
        }

        button.setOnClickListener {
            toggleBottomSheet(bottomSheetBehavior)
        }



        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {


                // React to state change
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events

            }
        })
    }

    private fun toggleBottomSheet(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    //binding location request
    private fun bindLocationRequest() {
        LocationRequest = LocationRequest()
        LocationRequest.setPriority(LocationRequest.priority)
        LocationRequest.setInterval(UPDATE_INTERVAL)
        LocationRequest.setFastestInterval(FASTEST_INTERVAL)
        //  LocationRequest.setSmallestDisplacement(5000f) //15 meter
    }


    //Auto GPS Enable Dialog
    private fun showAutoGPSDialog() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest)
        builder.setAlwaysShow(true)


        val task: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            d("GPS Enable -> start getting location")

            view.visibility = View.GONE
            cardTurnOnLocation.visibility = View.GONE

            startLocationUpdates()
        }

        task.addOnFailureListener {
            d("GPS Disable -> failed to start getting location")
            val resolvableApiException = it as ResolvableApiException
            resolvableApiException.startResolutionForResult(this@HomeMapActivity, REQUEST_SETTINGS)
        }


    }

    var filePath: String = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == ACTION_TAKE_VIDEO) {

            // filePath = getPath(data!!.getData()).toString();
            prepareVideoPlayer(data!!.getData(), view_video)

            return
        }

        EasyImagePicker.getInstance().passActivityResult(requestCode, resultCode, data, object :
            EasyImagePicker.easyPickerCallback {
            override fun onFailed(error: String?) {
                Toast.makeText(applicationContext, "Failed to pick image", Toast.LENGTH_LONG)
            }

            override fun onMediaFilePicked(result: String?) {

                filePath = result!!
                imgStory.visibility = View.VISIBLE
                imgStory.setImageURI(Uri.fromFile(File(result)))
            }


        })
    }

    open fun getPath(uri: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }


    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.btnSend -> {


                if (TextUtils.isEmpty(txtMessage.text.toString().trim())) {
                    toast(applicationContext, "Message is required")
                    return
                }
                if (filePath.isEmpty()) {
                    toast(applicationContext, "story media is missing")
                    return
                }


                if (filePath.contains("mp4")) {
                    JavaHelper.compress(filePath, this, this)
                } else {
                    val filename: String = filePath.substring(filePath.lastIndexOf("/") + 1)
                    val ftpHelper: FTPHelper = FTPHelper()
                    ftpHelper.init(this)
                    ftpHelper.AsyncTaskExample().execute(
                        filePath, filename
                    )

                }


//                    val model: UserStory = UserStory(
//                        txtMessage.text.toString(),
//                        KotlinHelper.getUsersData().SocialId,
//                        currentLocation.latitude.toString(),
//                        currentLocation.longitude.toString(),
//                        "",
//                        ""
//                    );
//                    // showProgressDialog()
//                    homeMapViewModel.uploadStoryData(model)


            }
            R.id.btnCamera -> {


                resetAll()
                EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID)
                    .openCamera()
            }
            R.id.btnGallery -> {
                resetAll()
                EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID)
                    .openGallery()
            }
            R.id.btnVideo -> {

                resetAll()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestVideoPermissions()
                } else {
                    openVideoRecorder()
                }
            }
            R.id.btnAudioRecorder -> {

                resetAll()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestRecordAudioPermissions()
                } else {
                    mediaTypeAudio.visibility = View.VISIBLE
                    mediaTypeVideo.visibility = View.GONE
                    chronometer.visibility = View.VISIBLE
                    imgBtRecord.visibility = View.VISIBLE
                    prepareRecording()
                    startRecording()
                }
            }
            //---------Audio Recorder----------//
            R.id.imgBtRecord -> {
                prepareRecording()
                startRecording()
            }

            R.id.imgBtStop -> {
                prepareStop()
                stopRecording()
            }

            R.id.imgViewPlay -> {
                if (!isPlaying && fileName != null) {
                    isPlaying = true
                    startPlaying()
                } else {
                    isPlaying = false
                    stopPlaying()
                }
            }
        }
    }

    //==============Audio Recorder Functions============//
    private fun prepareStop() {
        TransitionManager.beginDelayedTransition(llRecorder)
        //  imgBtRecord.visibility = View.VISIBLE
        imgBtStop.visibility = View.GONE
        llPlay.visibility = View.VISIBLE


        chronometer.visibility = View.GONE
        imgBtRecord.visibility = View.GONE
    }


    private fun prepareRecording() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.GONE
        imgBtStop.visibility = View.VISIBLE
        llPlay.visibility = View.GONE
    }

    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        //showing the play button
        imgViewPlay.setImageResource(R.drawable.ic_play_circle)
        chronometerAudio.stop()
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setMaxDuration(60000)
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val root = android.os.Environment.getExternalStorageDirectory()
        val file = File(root.absolutePath + "/FitFinder/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }

        fileName = root.absolutePath + "/FitFinder/Audios/" + (System.currentTimeMillis()
            .toString() + ".mp3")
        filePath = fileName!!
        // Log.d("filename", fileName)
        mRecorder!!.setOutputFile(fileName)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        lastProgress = 0
        seekBar.progress = 0
        stopPlaying()
        // making the imageView a stop button starting the chronometer
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        chronometer.setOnChronometerTickListener {
            if (it.text.toString().equals("00:31") && mRecorder != null) {

                prepareStop()
                stopRecording()
            }
        }
    }


    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()
        //showing the play button
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show()
    }


    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(fileName)
            mPlayer!!.prepare()
            mPlayer!!.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }

        //making the imageView pause button
        imgViewPlay.setImageResource(R.drawable.ic_pause_circle)

        seekBar.progress = lastProgress
        mPlayer!!.seekTo(lastProgress)
        seekBar.max = mPlayer!!.duration
        seekBarUpdate()

        chronometerAudio.start()
        chronometer.visibility = View.GONE
        imgBtRecord.visibility = View.GONE
        mPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            imgViewPlay.setImageResource(R.drawable.ic_play_circle)
            isPlaying = false
            chronometerAudio.stop()
            chronometerAudio.base = SystemClock.elapsedRealtime()
            mPlayer!!.seekTo(0)
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mPlayer != null && fromUser) {
                    mPlayer!!.seekTo(progress)
                    chronometerAudio.base =
                        SystemClock.elapsedRealtime() - mPlayer!!.currentPosition
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer!!.currentPosition
            seekBar.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }


    //request permission for record audio
    private fun requestRecordAudioPermissions() {

        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {

                        if (report.areAllPermissionsGranted()) {

                            mediaTypeAudio.visibility = View.VISIBLE
                            mediaTypeVideo.visibility = View.GONE
                            chronometer.visibility = View.VISIBLE
                            imgBtRecord.visibility = View.VISIBLE
                            prepareRecording()
                            startRecording()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
    }

    private fun requestVideoPermissions() {

        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {

                        if (report.areAllPermissionsGranted()) {

                            openVideoRecorder()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
    }

    private fun openVideoRecorder() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
        // create a file to save the video
        //  var fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set the image file name
        //takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);


        var provider = FileProvider.getUriForFile(
            this@HomeMapActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            getOutputMediaFile(2)!!
        )
        val mimeType = applicationContext.contentResolver.getType(provider)
        //  takeVideoIntent.setDataAndType(provider,mimeType)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, provider);
        takeVideoIntent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO)
    }

    /**
     * Video Player
     */

    fun prepareVideoPlayer(uri: Uri?, videoview: VideoView) {
        mediaTypeAudio.visibility = View.GONE
        mediaTypeVideo.visibility = View.VISIBLE
        try {
            // Start the MediaController
            val mediacontroller = MediaController(
                this@HomeMapActivity
            )
            mediacontroller.setAnchorView(videoview)
            // Get the URL from String VideoURL
            // val video = Uri.fromFile(File(path))
            videoview.setMediaController(mediacontroller)

//            videoview.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory().path
//                .toString() + "videocapture_example.mp4"))


//            var provider= FileProvider.getUriForFile(this@HomeMapActivity, BuildConfig.APPLICATION_ID+".provider", File(getPath(uri)))


            videoview.setVideoURI(uri)

//            val video: Uri = FileProvider.getUriForFile(
//                applicationContext,
//                getPackageName(),
//                File(getPath(uri))
//            )
//            videoview.setVideoURI(video)
        } catch (e: java.lang.Exception) {
            Log.e("Error", e.message!!)
            e.printStackTrace()
        }

        videoview.requestFocus()
        videoview.start()
        videoview.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(p0: MediaPlayer?) {
                videoview.start()
            }

        })
    }


    /** Create a file Uri for saving an image or video  */
    fun getOutputMediaFileUri(type: Int): Uri? {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video  */
    fun getOutputMediaFile(type: Int): File? {

        // Check that the SDCard is mounted
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "FitFinderVideo"
        )


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // output.setText("Failed to create directory MyCameraVideo.")
                Toast.makeText(
                    applicationContext, "Failed to create directory MyCameraVideo.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.")
                return null
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        val date = Date()
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(date.time)
        val mediaFile: File
        mediaFile = if (type == 2) {

            // For unique video file name appending current timeStamp with file name
//            File(
//                mediaStorageDir.path + File.separator +
//                        "VID_" + timeStamp + ".mp4"
//            )

            File(
                Environment.getExternalStorageDirectory().absolutePath + "/FitFinder/Audios/VID_" + (System.currentTimeMillis()
                    .toString() + ".mp4")
            )
        } else {
            return null
        }
        filePath = mediaFile.path
        return mediaFile
    }

    override fun getError(error: String) {
        if (error.contains("Successfully")) {
            resetAll()
        }
        this.toast(this, error)

    }

    override fun getSuccess(success: String) {
        if (success.equals("User Logout Successfully", true)) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            PrefsHelper.putString(Constants.Pref_UserData, "")
            this.finish()
        }
    }


    fun resetAll() {
        mediaTypeAudio.visibility = View.GONE
        mediaTypeVideo.visibility = View.GONE
        imgStory.visibility = View.GONE
        filePath = ""

    }


    fun showProgressDialog() {
        progressDialog = ProgressDialog(applicationContext)
        progressDialog.setMessage("Please wait...")
        progressDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        progressDialog.show()
    }

    override fun isCompress(success: Boolean, filePath: String) {

        val filename: String = filePath.substring(filePath.lastIndexOf("/") + 1)
        val ftpHelper: FTPHelper = FTPHelper()
        ftpHelper.init(this)
        ftpHelper.AsyncTaskExample().execute(filePath, filename)
    }

    override fun isFTPUpload(isUploaded: Boolean, fileName: String) {
        Log.d("HOMEMAPACTIVITY_", isUploaded.toString())
        Log.d("HOMEMAPACTIVITY_", fileName)

        val model: UserStory = UserStory(
            txtMessage.text.toString(),
            KotlinHelper.getUsersData().SocialId,
            currentLocation.latitude.toString(),
            currentLocation.longitude.toString(),
            "",
            "http://highbryds.com/fitfinder/stories/" + fileName
        );

        Log.d("HOMEMAPACTIVITY_" , JavaHelper.getAddress(this ,  currentLocation.latitude ,  currentLocation.longitude))

        // showProgressDialog()
        homeMapViewModel.uploadStoryData(model)
    }


}