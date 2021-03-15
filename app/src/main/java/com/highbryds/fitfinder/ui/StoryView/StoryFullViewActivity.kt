package com.highbryds.fitfinder.ui.StoryView

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.StoryCommentsAdapter
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.callbacks.onConfirmListner
import com.highbryds.fitfinder.commonHelper.*
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.model.StorySpam
import com.highbryds.fitfinder.sinch.SinchSdk
import com.highbryds.fitfinder.ui.Chatting.MessageActivity
import com.highbryds.fitfinder.ui.StoryComment.StoryCommentViewModel
import com.highbryds.fitfinder.vm.Profile.UserStoriesViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.petersamokhin.android.floatinghearts.HeartsRenderer
import com.petersamokhin.android.floatinghearts.HeartsView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_story_view.*
import kotlinx.android.synthetic.main.view_bottom_sheet_comments.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@AndroidEntryPoint
class StoryFullViewActivity : AppCompatActivity(), View.OnClickListener, ApiResponseCallBack {

    @Inject
    lateinit var storyFullViewModel: StoryFullViewModel

    @Inject
    lateinit var storyCommentViewModel: StoryCommentViewModel

    @Inject
    lateinit var userStoriesViewModel: UserStoriesViewModel


    lateinit var heartView: HeartsView;
    lateinit var storyData: NearbyStory
    lateinit var storyID: String
    var storyClapCounter = 0
     var  mediacontroller: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)
        heartView = HeartsView(applicationContext)
        bindBottomSheet()

        userStoriesViewModel.apiResponseCallBack = this


        val v: View =
            LayoutInflater.from(applicationContext).inflate(R.layout.view_popupwindow, null, false)
        val pw = PopupWindow(v, 500, 500, true)
//        imgAction.setOnClickListener {
//            pw.showAtLocation(
//                imgAction,
//                Gravity.CENTER,
//                0,
//                0
//            )
//        }


        //  })
        //  val popup_btn: Button = v.findViewById(R.id.popupbutton)

        // popup_btn.setOnClickListener(View.OnClickListener { popup_btn.setBackgroundColor(Color.RED) })

        imgViewPlay.setOnClickListener(this)
        imgClapStory.setOnClickListener(this)
        imgCancel.setOnClickListener(this)
        btnSendComment.setOnClickListener(this)


        val json = intent?.getStringExtra("storyData")
        storyData = Gson().fromJson(json, NearbyStory::class.java)
        storyID = storyData._id
        PrefsHelper.putString(Constants.Pref_ToOpenStoryAuto, "")


        with(storyData)
        {

            //set Cat and Title
            storyCat.text = Category
            storyTitle.text = storyName

            //show username in comment write section
            edtComment.setHint("comment on ${userData?.get(0)?.name}'s story")

            heartView.applyConfig(HeartsRenderer.Config(5f, 2f, 2f))

            rootview.addView(
                heartView,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            )
/*
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.clap_icon)
            Thread(Runnable {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                runOnUiThread {

                    for(i in 0 until storyClapData?.size!!)
                    {
                        heartView.emitHeart(HeartsView.Model(0, bitmap), HeartsView.MAX_Y_FULL)
                    }
                }
            }).start()*/

            if (mediaUrl.contains(".mp3")) {

                resetAllViews()
                containerAudioPlayer.visibility = View.VISIBLE
                if (!isPlaying) {
                    isPlaying = true
                    startPlaying()
                }

            } else if (mediaUrl.contains(".mp4")) {
                resetAllViews()
                videoAnimation.visibility = View.VISIBLE
                view_video.visibility = View.VISIBLE
                val videoUri: Uri = Uri.parse(storyData?.mediaUrl)
                prepareVideoPlayer(videoUri, view_video)
            } else {

                resetAllViews()
                imgStory.visibility = View.VISIBLE
                Glide
                    .with(applicationContext)
                    .load(mediaUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(imgStory);
            }

            txtStoryMessage.setText(storyName)

            if (userData != null && userData?.size!! > 0) let {
                Glide
                    .with(applicationContext)
                    .load(userData?.get(0)?.imageUrl)
                    .placeholder(R.drawable.man_cartoon)
                    .circleCrop()
                    .into(userImage);
                userName.setText(userData?.get(0)?.name)

            } else {
                userImage.setImageDrawable(
                    application.resources.getDrawable(
                        R.drawable
                            .man_cartoon
                    )
                )
            }
            if (storyClapData != null && storyClapData?.size!! > 0) let {

                storyClapCount.text = storyClapData?.size.toString()
                storyClapCounter = storyClapData?.size!!

            }
            else {
                storyClapCount.text = ""
            }


            //check user clap this story already or not
            val exist =
                if (storyData != null) storyData?.storyClapData?.find {
                    it.SocialId.equals(
                        KotlinHelper.getSocialID()
                    )
                } else null

            if (exist == null) {
                imgClapStory.setImageDrawable(applicationContext.resources.getDrawable(R.drawable.not_clap_icon))
            } else {
                imgClapStory.setImageDrawable(applicationContext.resources.getDrawable(R.drawable.clap_icon))
            }
        }


        storyFullViewModel.updateViews(KotlinHelper.getSocialID(), storyData?._id)

        storyFullViewModel.viewsdata.observe(this, Observer {

            viewsCount.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.scale_anim
                )
            )

            viewsCount.setText(it.toString())


        })

        storyFullViewModel.clapsData.observe(this, Observer {

            if (it == 1) {
                isClap = true
                storyClapCount.startAnimation(
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        R.anim.scale_anim
                    )
                )
                storyClapCounter += 1
                storyClapCount.setText(storyClapCounter.toString())

                imgClapStory.setImageDrawable(applicationContext.resources.getDrawable(R.drawable.clap_icon))
            } else {
                isClap = false
            }

        })

        //Observing comment data
        storyCommentViewModel.commentsData.observe(this, Observer {

            it?.let {
                edtComment.setText("")
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                adapter.clearData()
                adapter.addData(it)
                adapter.notifyDataSetChanged()
                storyCommentCount.setText(it?.size.toString())
            }
        })
        storyCommentViewModel.storyCommentsData.observe(this, Observer {

            it?.let {


                storyCommentCount.setText(it?.size.toString())
                adapter.clearData()
                adapter.addData(it)
                adapter.notifyDataSetChanged()

            }
        })

        storyCommentViewModel.fetchStoryComments(storyData?._id)
        bindStoryComments()

        storyCommentViewModel.apiErrorsCallBack = object : ApiResponseCallBack {
            override fun getError(error: String) {

                toast(applicationContext, error)
            }

            override fun getSuccess(success: String) {

                toast(applicationContext, success)
                adapter.deleteComment(deleteCommentPosition)
            }

        }
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
                videoPath,
                HashMap()
            ) else mediaMetadataRetriever.setDataSource(videoPath)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    /**
     * Video Player
     */
    var videoPlayer: MediaPlayer? = null

    lateinit var handler: Handler
    var flyingCount: Int = 0;
    override fun onResume() {
        super.onResume()

//        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.clap_icon)
//        bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, false)
//
//        handler = Handler(Looper.getMainLooper())
//        runnable =
//            Runnable {
//
//                storyData.storyClapData?.let {
//
//                    if (flyingCount != storyData?.storyClapData?.size!!) {
//                        heartView.emitHeart(
//                            HeartsView.Model(Random().nextInt(100), bitmap),
//                            HeartsView.MAX_Y_FULL
//                        )
//                        flyingCount++
//                        handler.postDelayed(runnable, 1000)
//                    } else {
//                        handler.removeCallbacks { runnable }
//                    }
//
//                }
//
//
//            }
//        handler.postDelayed(runnable, 1000)


//        Thread(Runnable {
//            try {
//                //Thread.sleep(500)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            runOnUiThread {
//
//                for(i in 0 until storyData?.storyClapData?.size!!)
//                {
//                    heartView.emitHeart(HeartsView.Model(0, bitmap), HeartsView.MAX_Y_FULL)
//                }
//            }
//        }).start()
    }

    fun prepareVideoPlayer(uri: Uri?, videoview: VideoView) {

        try {
            // Start the MediaController
            mediacontroller = MediaController(
                this
            )
            mediacontroller?.setAnchorView(videoview)
            videoview.setMediaController(mediacontroller)

            videoview.setVideoURI(uri)

        } catch (e: java.lang.Exception) {
            Log.e("Error", e.message!!)
            e.printStackTrace()
        }

        videoview.requestFocus()
        // videoview.start()
        videoview.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(p0: MediaPlayer?) {

                videoPlayer = p0!!
                videoview.start()
                videoAnimation.visibility = View.INVISIBLE

            }

        })
    }

    var deleteCommentPosition: Int = 0;
    lateinit var adapter: StoryCommentsAdapter
    private fun bindStoryComments() {
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter = StoryCommentsAdapter(applicationContext, arrayListOf())
        recycler_view.addItemDecoration(
            DividerItemDecoration(
                recycler_view.context,
                (recycler_view.layoutManager as LinearLayoutManager).orientation
            )
        )
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recycler_view.adapter as StoryCommentsAdapter
//                adapter.deleteComment(viewHolder.adapterPosition)
                val storyComment: StoryComment = adapter.getitem(viewHolder.adapterPosition)
                storyCommentViewModel.deleteStoryComments(storyComment?._id, storyComment?.story_id)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycler_view)
        recycler_view.adapter = adapter

        adapter.storyCallback = object : StoryCallback {
            override fun storyItemPosition(position: Int) {

                deleteCommentPosition = position
                showConfirmationDialog()
            }

        }
    }

    //=========Audio Player
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    var fileName: String? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private var isPlaying = false

    private fun startPlaying() {
        audioAnimation.visibility = View.VISIBLE
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(
                if (storyData?.mediaUrl.contains("https")) storyData?.mediaUrl.replace(
                    "https",
                    "http"
                ) else storyData?.mediaUrl
            )
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
        chronometerAudio.visibility = View.VISIBLE

        mPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            imgViewPlay.setImageResource(R.drawable.ic_play_circle)
            isPlaying = false
            audioAnimation.visibility = View.INVISIBLE
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

    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.imgViewPlay -> {
                if (!isPlaying) {
                    isPlaying = true
                    startPlaying()
                } else {
                    isPlaying = false
                    stopPlaying()
                }

            }
            R.id.imgCancel -> {

            //    setResult(777)
                finish()
            }
            R.id.btnSendComment -> {

                if (TextUtils.isEmpty(edtComment.text.toString())) {
                    toast(applicationContext, "Please enter comment")
                    return
                }
                btnSendComment.startAnimation(
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        R.anim.scale_anim
                    )
                )
                storyCommentViewModel.addStoryComment(
                    KotlinHelper.getSocialID(),
                    storyData?._id,
                    edtComment.text.toString()
                )

            }
            R.id.imgClapStory -> {


                val exist =
                    if (storyData != null) storyData?.storyClapData?.find {
                        it.SocialId.equals(
                            KotlinHelper.getSocialID()
                        )
                    } else null
                if (exist == null && isClap == false) {
                    imgClap.startAnimation(
                        AnimationUtils.loadAnimation(
                            applicationContext,
                            R.anim.scale_anim
                        )
                    )
                    storyFullViewModel.insetStoryClap(KotlinHelper.getSocialID(), storyData?._id)
                } else {
                    // toast(this, "you have already clap this story")
                }

            }

        }
    }

    var isClap: Boolean = false
    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        audioAnimation.visibility = View.INVISIBLE
        //showing the play button
        imgViewPlay.setImageResource(R.drawable.ic_play_circle)
        chronometerAudio.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (storyData?.mediaUrl.contains(".mp3")) {
            stopPlaying()
        } else if (storyData?.mediaUrl.contains(".mp4")) {
            videoPlayer?.release()
        }
    }

    fun resetAllViews() {
        mediacontroller?.let {
              mediacontroller?.hide()
        }

        videoPlayer?.stop()
        //view_video.setMediaController(null)
        containerAudioPlayer.visibility = View.GONE
        view_video.visibility = View.GONE
        imgStory.visibility = View.GONE
    }

    fun showPopup(v: View?) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.actions, popup.menu)


        if (!(storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId))) {
            popup.getMenu().findItem(R.id.delete).setVisible(false);
        }

        if (storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId)) {
            popup.getMenu().findItem(R.id.report).setVisible(false);
        }

        if (storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId)) {
            popup.getMenu().findItem(R.id.profile).setVisible(false);
        }

        if (storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId) || storyData.enableChat == 0) {
            popup.getMenu().findItem(R.id.chat).setVisible(false);
        }

        if (storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId) ||
            storyData.enableCall == 0
        ) {
            popup.getMenu().findItem(R.id.call).setVisible(false);
        }


        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.delete -> {
                        if (storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId)) {
                            KotlinHelper.alertDialog(
                                "Alert",
                                "Are you sure you want to delete this story?",
                                this@StoryFullViewActivity,
                                object :
                                    onConfirmListner {
                                    override fun onClick() {
                                        userStoriesViewModel.deactivate(storyID)
                                    }
                                })
                        } else {
                            this@StoryFullViewActivity.toast(
                                this@StoryFullViewActivity,
                                "You can't delete other story"
                            )
                        }
                        return true
                    }
                    R.id.chat -> {
                        SinchSdk.RECIPENT_ID = storyData.userData?.get(0)?.SocialId
                        SinchSdk.RECIPENT_NAME = storyData.userData?.get(0)?.name
                        SinchSdk.USER_ID = KotlinHelper.getUsersData().SocialId


                        val intent = Intent(this@StoryFullViewActivity, MessageActivity::class.java)
                        startActivity(intent)
                        return true
                    }
                    R.id.report -> {
                        if (storyData.userData?.get(0)?.SocialId.equals(KotlinHelper.getUsersData().SocialId)) {
                            this@StoryFullViewActivity.toast(
                                this@StoryFullViewActivity,
                                "You can't report own story"
                            )
                        } else {
                            val spam =
                                StorySpam(storyData._id, storyData.userData!!.get(0).SocialId)
                            storyFullViewModel.spamStoryById(spam)
                        }
                    }
                    R.id.call -> {


                        Dexter.withContext(this@StoryFullViewActivity)
                            .withPermission(android.Manifest.permission.CALL_PHONE)
                            .withListener(object : PermissionListener {
                                override
                                fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data =
                                        Uri.parse("tel:" + storyData.userData?.get(0)?.cellNumber)
                                    startActivity(intent)
                                }

                                override
                                fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                }

                                override
                                fun onPermissionRationaleShouldBeShown(
                                    permission: PermissionRequest?,
                                    token: PermissionToken?
                                ) {
                                    token!!.continuePermissionRequest();
                                }
                            }
                            ).check()


                    }

                    R.id.profile -> {

                    }
                }
                // Toast.makeText(this@StoryFullViewActivity, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                return true
            }
        })
        popup.show()
    }

    override fun getError(error: String) {
        this.toast(this, error.toString())
    }

    override fun getSuccess(success: String) {
        this.finish()
        this.toast(this, success.toString())
        //PrefsHelper.putBoolean(Constants.Pref_IsStoryDeleted , true)
    }

    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    //bottom sheet binding
    private fun bindBottomSheet() {

        viewComment.setOnClickListener {

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout_comment)

        coordinatorLayout.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.getState() === BottomSheetBehavior.STATE_HALF_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        })


        bottom_sheet_layout_comment.setOnClickListener {
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
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }


    fun showConfirmationDialog() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this@StoryFullViewActivity)

        // Set the alert dialog title
        builder.setTitle("Delete")

        // Display a message on alert dialog
        builder.setMessage("Are you sure want to delete this comment?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES") { dialog, which ->

            val storyComment: StoryComment = adapter.getitem(deleteCommentPosition)
            storyCommentViewModel.deleteStoryComments(storyComment?._id, storyComment?.story_id)
            dialog.dismiss()
        }


        // Display a negative button on alert dialog
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }


        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        resetAllViews()
    }
}