package com.highbryds.fitfinder.ui.Main

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.*
import android.location.Location
import android.location.LocationManager
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
import android.view.animation.AnimationUtils
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
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.highbryds.fitfinder.BuildConfig
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.MyInfoWindowAdapter
import com.highbryds.fitfinder.adapters.TrendingStoriesAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.FTPCallback
import com.highbryds.fitfinder.callbacks.onConfirmListner
import com.highbryds.fitfinder.callbacks.videoCompressionCallback
import com.highbryds.fitfinder.commonHelper.*
import com.highbryds.fitfinder.model.MediaType
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.UserStory
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Auth.LoginActivity
import com.highbryds.fitfinder.ui.BaseActivity
import com.highbryds.fitfinder.ui.Chatting.MessagesListActivity
import com.highbryds.fitfinder.ui.Profile.MyProfile
import com.highbryds.fitfinder.ui.Profile.UserProfileSetting
import com.highbryds.fitfinder.ui.Profile.UserStories
import com.highbryds.fitfinder.ui.StoryView.StoryFullViewActivity
import com.highbryds.fitfinder.ui.carpool.CarpoolSelectionActivity
import com.highbryds.fitfinder.vm.AuthViewModels.LogoutViewModel
import com.highbryds.fitfinder.vm.Main.StoryViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.log4k.d
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.util.addStickyFooterItem
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.pakdev.easypicker.utils.EasyImagePicker
import com.stfalcon.multiimageview.MultiImageView
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home_map.*
import kotlinx.android.synthetic.main.activity_home_map.coordinatorLayout
import kotlinx.android.synthetic.main.activity_story_view.*
import kotlinx.android.synthetic.main.record_audio_activity.chronometer
import kotlinx.android.synthetic.main.record_audio_activity.imgBtRecord
import kotlinx.android.synthetic.main.record_audio_activity.imgBtStop
import kotlinx.android.synthetic.main.record_audio_activity.imgViewPlay
import kotlinx.android.synthetic.main.record_audio_activity.llPlay
import kotlinx.android.synthetic.main.record_audio_activity.llRecorder
import kotlinx.android.synthetic.main.record_audio_activity.seekBar
import kotlinx.android.synthetic.main.view_audio_recorder.chronometerAudio
import kotlinx.android.synthetic.main.view_bottom_sheet.*
import kotlinx.android.synthetic.main.view_bottom_sheet.btnSend
import kotlinx.android.synthetic.main.view_bottom_sheet.imgStory
import kotlinx.android.synthetic.main.view_bottom_sheet.txtMessage
import kotlinx.android.synthetic.main.view_category_selection.*
import kotlinx.android.synthetic.main.view_multiple_storyview.*
import kotlinx.android.synthetic.main.view_turn_location_on.*
import kotlinx.android.synthetic.main.view_video_recorder.*
import kotlinx.android.synthetic.main.view_video_recorder.view_video
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


@AndroidEntryPoint
open class HomeMapActivity : BaseActivity(), OnMapReadyCallback, View.OnClickListener,
    ApiResponseCallBack, videoCompressionCallback, FTPCallback {


    lateinit var provider: Uri
    var mediacontroller: MediaController? = null

    private lateinit var mediaType: MediaType
    private val TAG = HomeMapActivity::class.java!!.getSimpleName()

    private val UPDATE_INTERVAL: Long = 5000
    private val REQUEST_SETTINGS: Int = 0x2
    private val FASTEST_INTERVAL: Long = 5000 // = 5 seconds

    private lateinit var LocationRequest: LocationRequest

    private lateinit var mMapGoogleFragment: SupportMapFragment
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    //lateinit var reciever: GpsLocationReceiver

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
    private var chipText: String? = null
    private var chipTextHelp: String? = ""
    private var enableCall: String? = "0"
    private var enableChat: String? = "0"
    var headerView: AccountHeaderView? = null
    var isCamera: Boolean = false;

    //========End=====//
    private val SELECT_VIDEO = 1
    private val ACTION_TAKE_VIDEO = 122

    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var homeMapViewModel: HomeMapViewModel

    @Inject
    lateinit var storyViewModel: StoryViewModel

    @Inject
    lateinit var logoutViewModel: LogoutViewModel

    // lateinit var adapter: TrendingStoriesAdapter

    var isTrendingStoryView: Boolean = false

    companion object {
        var isStoryDeletedFromSection: Boolean = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_map)

        //    bindNavigationDrawer(toolbar)
        mediaType = MediaType.TEXT

        homeMapViewModel.apiErrorsCallBack = this
        logoutViewModel.apiResponseCallBack = this
        storyViewModel.apiErrorsCallBack = this

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

        multiStoryView.setOnClickListener {

            if (isTrendingStoryView) {
                isTrendingStoryView = false
                recycler_view.visibility = View.GONE
            } else {

                isTrendingStoryView = true
                recycler_view.visibility = View.VISIBLE

            }
        }

        /**
         * bind image to bottom sheet
         */
        Glide
            .with(applicationContext)
            .load(KotlinHelper.getUsersData()?.imageUrl)
            .circleCrop()
            .placeholder(R.drawable.man_cartoon)
            .into(imgProfile);

        homeMapViewModel.categoriesData.observe(this, androidx.lifecycle.Observer {

            it?.let {

                for ((index, category) in it.withIndex()) {
                    val mChip = this.layoutInflater.inflate(R.layout.view_chip, null, false) as Chip
                    mChip.text = category
                    mChip.id = index
                    mChip.isChipIconVisible = true
                    chipGroup.addView(mChip)
                    chipGroupHelp.visibility = View.GONE
                    chipReset.visibility = View.GONE
                    chipGroup.visibility = View.VISIBLE
                }
            }

        })

        homeMapViewModel.helpcategories.observe(this, androidx.lifecycle.Observer {
            it?.let {
                for ((index, category) in it.withIndex()) {
                    val mChip = this.layoutInflater.inflate(R.layout.view_chip, null, false) as Chip
                    mChip.text = category
                    mChip.id = index
                    mChip.isChipIconVisible = true
                    chipGroupHelp.addView(mChip)
                }
            }
        })

        chipGroup.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId != -1) {
                val chip: Chip = chipGroup.findViewById(checkedId)
                if (chip.text.toString().contains("Help", true)) {
                    chipGroupHelp.visibility = View.VISIBLE
                    chipReset.visibility = View.VISIBLE
                    chipGroup.visibility = View.GONE

                } else {
                    chipGroupHelp.visibility = View.GONE
                    chipReset.visibility = View.GONE
                    chipGroup.visibility = View.VISIBLE
                }
                chipText = chip.text.toString()
            }
        }

        chipGroupHelp.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                val chip: Chip = chipGroupHelp.findViewById(checkedId)
                chipGroupHelp.visibility = View.GONE
                chipGroupOption.visibility = View.VISIBLE
                chipTextHelp = chip.text.toString()
            }
        }

        call.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableCall = "1"
            } else {
                enableCall = "0"
            }
        }

        chat.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableChat = "1"
            } else {
                enableChat = "0"
            }
        }



        chipReset.setOnClickListener {
            chipGroupHelp.visibility = View.GONE
            chipReset.visibility = View.GONE
            chipGroupOption.visibility = View.GONE
            chipGroup.visibility = View.VISIBLE
            chipGroup.clearCheck()
            chipGroupHelp.clearCheck()
            chipText = ""
            chipTextHelp = ""
            enableChat = ""
            enableCall = ""
        }

        homeMapViewModel.trendingStoriesData.observe(this, androidx.lifecycle.Observer {

            it?.let {
                adapter.removeData()
                adapter.addData(it)
                adapter.notifyDataSetChanged()
                if (it.isNotEmpty()) {
                    multiStoryView.visibility = View.VISIBLE
                    showStoryImage(storyImg1, it.get(Random().nextInt(it.size))?.mediaUrl)
                    showStoryImage(storyImg2, it.get(Random().nextInt(it.size))?.mediaUrl)
                    showStoryImage(storyImg3, it.get(Random().nextInt(it.size))?.mediaUrl)
                }

            }


        })

        homeMapViewModel.observeAllNearByStories().observe(this, androidx.lifecycle.Observer {

            //here checking newly uplaod story by user
            if (it.size == 1) {
                loadingProgress.visibility = View.GONE
                //for new story added by user and append in map
                //resetAll()
                txtMessage.setText("")
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                moveGoogleMap(LatLng(it.get(0).latitude, it.get(0).longitude))
            }
            toast(applicationContext, "Stories fetching...")
            for (item: NearbyStory in it) {
                Log.d("StoryData", item.mediaUrl)
                if (item.latitude != 0.0) {
                    //   mGoogleMap.clear()
                    addStoryMarker(this, item)
                }
//                for (item: NearbyStory in it) {
//                    // Log.d("StoryData", item.mediaUrl)
//                    if (item.latitude != 0.0) {
//                        //   mGoogleMap.clear()
//                        addStoryMarker(this, item)
//                    }
//
//                }
            }


        })


        drawerSetup()
        IV_Slider.setOnClickListener {
            slider.drawerLayout?.openDrawer(slider)
            //val toClear: Int = slider.selectedItemPosition

        }

        carpool.setOnClickListener {
            if (KotlinHelper.getUsersData().cellNumber != null) {
                if (android.util.Patterns.PHONE.matcher(KotlinHelper.getUsersData().cellNumber)
                        .matches()
                ) {
                    val intent = Intent(this, CarpoolSelectionActivity::class.java)
                    startActivity(intent)
                } else {

                }
            } else {
                this.toast(this, "Please update profile first")
            }


        }

        //Refresh Stories
        btnRefresh.setOnClickListener {

            btnRefresh.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.scale_anim
                )
            )
            spin_kit.visibility = View.VISIBLE
            currentLocation?.let {


                with(currentLocation)
                {
                    homeMapViewModel.fetchNearByStoriesData(
                        latitude?.toString(),
                        longitude?.toString()
                    )
                }
            }

        }


    }

    class getBitmapImage(image: MultiImageView) :
        AsyncTask<String?, Void?, Bitmap?>() {
        var multiImage: MultiImageView

        init {
            multiImage = image
        }

        override fun doInBackground(vararg urls: String?): Bitmap? {
            return try {
                val url = URL(urls[0])
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input: InputStream = connection.getInputStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            bitmap?.let {
                multiImage.addImage(bitmap)

            }
        }
    }

    fun showStoryImage(storyview: CircleImageView, url: String) {
        Glide
            .with(applicationContext)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .into(storyview);
    }

    fun drawerSetup() {

        try {
            // Create the AccountHeader
            //ProfileDrawerItem().textColor = ColorHolder.fromColor(Color.WHITE).
            headerView = AccountHeaderView(this).apply {
                attachToSliderView(slider) // attach to the slider
                addProfiles(
                    ProfileDrawerItem().withName(KotlinHelper.getUsersData().name).withEmail(
                        KotlinHelper.getUsersData().emailAdd
                    )
                )
                slider.accountHeader?.currentProfileName?.setTextColor(Color.WHITE)
                slider.accountHeader?.currentProfileEmail?.setTextColor(Color.WHITE)
                slider.accountHeader?.accountHeaderBackground?.setBackgroundResource(R.drawable.background_profile)

                // slider.accountHeader?.accountHeader = getDrawable(R.drawable.background_profile)

                // .textColor = ColorStateList.valueOf(Color.WHITE)

                onAccountHeaderListener = { view, profile, current ->
                    // react to profile changes
                    false
                }
            }


            headerView
            headerView!!.selectionListEnabledForSingleProfile = false


            val imageView = headerView!!.currentProfileView
            Glide
                .with(this)
                .load(KotlinHelper.getUsersData().imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView);

            //if you want to update the items at a later time it is recommended to keep it in a variable
            val home = PrimaryDrawerItem().withIdentifier(1).withName("Home")

            val story = PrimaryDrawerItem().withIdentifier(2).withName("Contributions")
            val chat = PrimaryDrawerItem().withIdentifier(3).withName("Messages")
            val profile = PrimaryDrawerItem().withIdentifier(4).withName("Profile")
            val settings = PrimaryDrawerItem().withIdentifier(5).withName("Settings")
            val privayPolicy = PrimaryDrawerItem().withIdentifier(5).withName("Privacy Policy")
            val logout = PrimaryDrawerItem().withIdentifier(6).withName("Logout")


            // get the reference to the slider and add the items
            slider.itemAdapter.add(
                home, profile, chat, story,
                // DividerDrawerItem(),
                settings,
                privayPolicy,
                logout
            )

            slider.headerView = headerView
            slider.addStickyFooterItem(
                PrimaryDrawerItem().withName("Powered by HIGHBRYDS | Version  " + BuildConfig.VERSION_NAME)
                    .withTypeface(
                        Typeface.DEFAULT_BOLD
                    )
            )

            // specify a click listener
            slider.onDrawerItemClickListener = { v, drawerItem, position ->
                when (position) {
                    1 -> {
                        //this.toast(this, "Home")
                    }
                    2 -> {
                        // val intent = Intent(this, UserProfileMain::class.java)
                        val intent = Intent(this, MyProfile::class.java)
                        startActivity(intent)
                    }
                    3 -> {
                        SinchSdk.USER_ID = KotlinHelper.getUsersData().SocialId
                        val intent = Intent(this, MessagesListActivity::class.java)
                        startActivity(intent)
                    }
                    4 -> {
                        val intent = Intent(this, UserStories::class.java)
                        startActivity(intent)
                    }
                    6 -> {
                        val intent = Intent(this, UserProfileSetting::class.java)
                        startActivity(intent)
                    }
                    7 -> {

                        KotlinHelper.alertDialog("Alert", "Are you sure you want to logout ?", this,
                            object : onConfirmListner {
                                override fun onClick() {
                                    progress_bar.visibility = View.VISIBLE
                                    logoutViewModel.logoutUser(KotlinHelper.getUsersData().SocialId)
                                    PrefsHelper.putBoolean(Constants.Pref_IsLogin, false)
                                }
                            })

                    }
                }
                false
            }

        } catch (e: Exception) {
        }


    }


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
                .title(story.Category ?: "Unknown")
                .snippet(Gson().toJson(story))
                //.snippet(story.storyName + "")
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

    private fun drawer() {

        //drawer header
        // Create the AccountHeader
        val headerView = AccountHeaderView(this).apply {
            attachToSliderView(slider) // attach to the slider
            addProfiles(
                ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com")
                    .withIcon(getResources().getDrawable(R.drawable.clap_icon))
            )
            onAccountHeaderListener = { view, profile, current ->
                // react to profile changes
                false
            }
        }

        val item1 = PrimaryDrawerItem().withIdentifier(1).withName("Home")
        val item2 = PrimaryDrawerItem().withIdentifier(2).withName("Profile")

        slider.itemAdapter.add(
            item1,
            item2,
            SecondaryDrawerItem().withName("Setting")
        )

        slider.onDrawerItemClickListener = { v, drawerItem, position ->
            // do something with the clicked item :D
            false
        }

        slider.headerView = headerView

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

//        val filter: IntentFilter = IntentFilter()
//        filter.addAction("android.location.PROVIDERS_CHANGED");
//        reciever = GpsLocationReceiver()
//        registerReceiver(reciever, filter)

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED)
        registerReceiver(gpsSwitchStateReceiver, filter)


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

        mGoogleMap.setOnInfoWindowClickListener {

            val intent = Intent(this, StoryFullViewActivity::class.java)
            intent.putExtra("storyData", it.snippet)
            startActivityForResult(intent, 777)

        }
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
        mGoogleMap.uiSettings.isCompassEnabled = false
    }

    lateinit var adapter: TrendingStoriesAdapter
    private fun setupTrendingStories() {
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapter = TrendingStoriesAdapter(arrayListOf(), applicationContext)
//        recycler_view.addItemDecoration(
//            DividerItemDecoration(
//                recycler_view.context,
//                (recycler_view.layoutManager as LinearLayoutManager).orientation
//            )
//        )
        recycler_view.adapter = adapter

    }

    //request permission for location
    private fun requestLocationPermissions() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withActivity(this@HomeMapActivity)
                .withPermissions(
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
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
                        token?.continuePermissionRequest()
                    }

                }).check()
        } else {
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

    }


    //binding Fused location provider client and location request for getting location from GPS
    private fun bindGoogleFusedLocationClient() {

        bindLocationRequest()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    override fun onResume() {
        super.onResume()

        if (bottomSheetBehavior.getState() === BottomSheetBehavior.STATE_EXPANDED && isCamera) {
            mediaTypeVideo.visibility = View.VISIBLE
        } else {
            view_video?.clearAnimation();
            view_video?.suspend(); // clears media player
            view_video?.setVideoURI(null);
            // view_video?.stopPlayback()
            view_video?.stopPlayback()
            mediacontroller?.hide()
            mediaTypeVideo.visibility = View.GONE
        }
        // if (view_video.isPlaying){
        //view_video.stopPlayback()
        //mediacontroller?.hide()
        // }

        slider.setSelectionAtPosition(1)
        val imageView = headerView!!.currentProfileView
        Glide
            .with(this)
            .load(KotlinHelper.getUsersData().imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(imageView);

        //  headerView.updateProfile()

        registerLocationBroadcast()
        //val bundle = intent.extras
        if (!PrefsHelper.getString(Constants.Pref_ToOpenStoryAuto, "").equals("")) {
            storyViewModel.getStoryById(PrefsHelper.getString(Constants.Pref_ToOpenStoryAuto, ""))
            storyViewModel.singleStory.observe(this, androidx.lifecycle.Observer {
                it?.let {
                    val intent = Intent(this, StoryFullViewActivity::class.java)
                    val gson = Gson()
                    val json = gson.toJson(it.data.get(0))
                    intent.putExtra("storyData", json)
                    startActivityForResult(intent, 777)
                }
            })
        }

        if (isStoryDeletedFromSection) {
            isStoryDeletedFromSection = false
            mGoogleMap.clear()
            spin_kit.visibility = View.VISIBLE
            currentLocation?.let {


                with(currentLocation)
                {
                    homeMapViewModel.fetchNearByStoriesData(
                        latitude?.toString(),
                        longitude?.toString()
                    )
                }
            }
        }

    }

    private val gpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override
        fun onReceive(context: Context, intent: Intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.action)) {
                val locationManager: LocationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled: Boolean =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled: Boolean =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (isGpsEnabled || isNetworkEnabled) {
                    Timer("SettingUp", false).schedule(3000) {
                        showAutoGPSDialog()
                    }

                } else {
                    // Handle Location turned OFF
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //Remove location updates
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback!!)
        }

//        if (reciever != null) {
//            unregisterReceiver(reciever)
//        }
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


        if (requestCode == 777 && resultCode == Activity.RESULT_OK) {
            homeMapViewModel.fetchNearByStoriesData(
                currentLocation.latitude.toString(),
                currentLocation.longitude.toString()
            )
        } else if (requestCode == ACTION_TAKE_VIDEO) {

            // filePath = getPath(data!!.getData()).toString();
            // prepareVideoPlayer(data!!.getData(), view_video)
            prepareVideoPlayer(provider, view_video)

            return
        } else if (requestCode == EasyImagePicker.REQUEST_TAKE_PHOTO || requestCode == EasyImagePicker.REQUEST_GALLERY_PHOTO) {
//            if (data?.data == null)
//                return
            try {
                EasyImagePicker.getInstance()
                    .passActivityResult(requestCode, resultCode, data, object :
                        EasyImagePicker.easyPickerCallback {
                        override fun onFailed(error: String?) {
                            Toast.makeText(
                                applicationContext,
                                "Failed to pick image",
                                Toast.LENGTH_LONG
                            )
                        }

                        override fun onMediaFilePicked(result: String?) {

                            result


                            if (requestCode == EasyImagePicker.REQUEST_TAKE_PHOTO) {

                                val file: File = File(result)
                                filePath = file.absolutePath
                                val myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
                                imgStory.visibility = View.VISIBLE
                                imgStory.setImageBitmap(myBitmap)

                            } else {

                                val uri: Uri? = data?.data
                                val file: File = File(PathUtil.getPath(this@HomeMapActivity, uri))
                                filePath = JavaHelper.CompressPic(file, this@HomeMapActivity)

                                // filePath = result!!
                                imgStory.visibility = View.VISIBLE
                                imgStory.setImageURI(Uri.fromFile(File(result)))
                            }

                        }


                    })
            } catch (e: Exception) {
            }

        }
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

                if (chipText == null) {
                    toast(applicationContext, "Please select category")
                    return
                }

                if (chipText.equals(
                        "Help",
                        true
                    ) && (chipTextHelp.isNullOrEmpty() || chipTextHelp.isNullOrBlank())
                ) {
                    toast(applicationContext, "Please select help category")
                    return
                }

                if (TextUtils.isEmpty(txtMessage.text.toString().trim())) {
                    toast(applicationContext, "Message is required")
                    return
                }

                // if (filePath.isEmpty()) {
                // toast(applicationContext, "story media is missing")
                // return
                // filePath=null
                //   }

                btnSend.startAnimation(
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        R.anim.scale_anim
                    )
                )

                loadingProgress.visibility = View.VISIBLE


                when (mediaType) {
                    MediaType.AUDIO -> {

                        // if (mRecorder != null) {
                        prepareStop()
                        stopRecording()
                        val filename: String = filePath.substring(filePath.lastIndexOf("/") + 1)
                        val ftpHelper: FTPHelper = FTPHelper()
                        ftpHelper.init(this)
                        ftpHelper.AsyncTaskExample().execute(filePath, filename)
                        // }
                    }
                    MediaType.VIDEO -> {
                        JavaHelper.compress(filePath, this, this)
                    }

                    MediaType.TEXT -> {
                        val model = UserStory(
                            JavaHelper.badWordReplace(txtMessage.text.toString()),
                            KotlinHelper.getUsersData().SocialId,
                            currentLocation.latitude.toString(),
                            currentLocation.longitude.toString(),
                            "",
                            "",
                            chipText!!,
                            JavaHelper.getAddress(
                                this,
                                currentLocation.latitude,
                                currentLocation.longitude
                            ), enableCall, enableChat, chipTextHelp, "NO"
                        );
                        homeMapViewModel.uploadStoryData(model)
                    }
                    else -> {

                        val filename: String = filePath.substring(filePath.lastIndexOf("/") + 1)
                        val ftpHelper: FTPHelper = FTPHelper()
                        ftpHelper.init(this)
                        ftpHelper.AsyncTaskExample().execute(filePath, filename)

                    }


//                    val model: UserStory = UserStory(
//                        txtMessage.text.toString(),
//                        KotlinHelper.getUsersData().SocialId,
//                        currentLocation.latitude.toString(),
//                        currentLocation.longitude.toString(),
//                        "",
//                        ""
//                    );
                    // showProgressDialog()
                    //   homeMapViewModel.uploadStoryData(model)

                }
            }
            R.id.btnCamera -> {

                mediaType = MediaType.CAMERA
                resetAll()
                EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID)
                    .openCamera()
                isCamera = false;
            }
            R.id.btnGallery -> {

                mediaType = MediaType.GALERY
                resetAll()
                EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID)
                    .openGallery()
                isCamera = false;
            }
            R.id.btnVideo -> {

                mediaType = MediaType.VIDEO
                resetAll()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestVideoPermissions()
                } else {
                    openVideoRecorder()
                }
                isCamera = false;
            }
            R.id.btnAudioRecorder -> {

                mediaType = MediaType.AUDIO
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
                isCamera = false;
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


        provider = FileProvider.getUriForFile(
            this@HomeMapActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            getOutputMediaFile(2)!!
        )
        val mimeType = applicationContext.contentResolver.getType(provider)
        //  takeVideoIntent.setDataAndType(provider,mimeType)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, provider)

        takeVideoIntent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO)
    }

    /**
     * Video Player
     */

    fun prepareVideoPlayer(uri: Uri?, videoview: VideoView) {
        isCamera = true
        mediaTypeAudio.visibility = View.GONE
        mediaTypeVideo.visibility = View.VISIBLE
        try {
            // Start the MediaController
            mediacontroller = MediaController(
                this@HomeMapActivity
            )
            mediacontroller?.setAnchorView(videoview)
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
        loadingProgress.visibility = View.GONE
        progress_bar.visibility = View.GONE
        spin_kit.visibility = View.GONE
    }

    override fun getSuccess(success: String) {
        loadingProgress.visibility = View.GONE
        if (success.equals("User Logout Successfully", true)) {
            progress_bar.visibility = View.GONE
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            PrefsHelper.putString(Constants.Pref_UserData, "")
            PrefsHelper.putBoolean(Constants.Pref_IsLogin, false)
            PrefsHelper.putString(Constants.Pref_isOTPMobile, "")
            PrefsHelper.putBoolean(Constants.Pref_isOTPVerifed, false)
            PrefsHelper.clear().commit()
            this.finish()
        }
        resetAll()
        spin_kit.visibility = View.GONE
//        if (view_video.isPlaying) {
//            view_video.stopPlayback()
//        }
//        view_video.visibility = View.GONE
//        mediacontroller?.hide()
    }

    fun resetAll() {
        mediaTypeAudio.visibility = View.GONE
        mediaTypeVideo.visibility = View.GONE
        imgStory.visibility = View.GONE
        filePath = ""

        view_video?.stopPlayback()
        view_video?.stopPlayback()

        mediacontroller?.hide()
        mediaTypeVideo.visibility = View.GONE
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
            JavaHelper.badWordReplace(txtMessage.text.toString()),
            KotlinHelper.getUsersData().SocialId,
            currentLocation.latitude.toString(),
            currentLocation.longitude.toString(),
            "",
            "http://highbryds.com/fitfinder/stories/" + fileName,
            chipText!!,
            JavaHelper.getAddress(this, currentLocation.latitude, currentLocation.longitude),
            enableCall, enableChat, chipTextHelp, ""
        )

//        Log.d(
//            "HOMEMAPACTIVITY_", JavaHelper.getAddress(
//                this,
//                currentLocation.latitude,
//                currentLocation.longitude
//            )
//        )

        // showProgressDialog()
        homeMapViewModel.uploadStoryData(model)
    }


    override fun onBackPressed() {
    }
}



