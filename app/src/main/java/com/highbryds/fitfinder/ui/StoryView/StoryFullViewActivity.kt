package com.highbryds.fitfinder.ui.StoryView

import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapters.StoryCommentsAdapter
import com.highbryds.fitfinder.adapters.TrendingStoriesAdapter
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.model.TrendingStory
import com.highbryds.fitfinder.ui.StoryComment.StoryCommentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_story_view.*
import kotlinx.android.synthetic.main.activity_story_view.audioAnimation
import kotlinx.android.synthetic.main.view_bottom_sheet_comments.*
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class StoryFullViewActivity : AppCompatActivity(),View.OnClickListener {

   @Inject
   lateinit var storyFullViewModel: StoryFullViewModel

    @Inject
    lateinit var storyCommentViewModel: StoryCommentViewModel

    lateinit var storyData:NearbyStory

    var storyClapCounter=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)




        val v: View =
            LayoutInflater.from(applicationContext).inflate(R.layout.view_popupwindow, null, false)
        val pw = PopupWindow(v, 500, 500, true)
        imgAction.setOnClickListener {
            pw.showAtLocation(
                imgAction,
                Gravity.CENTER,
                0,
                0
            )
        }




      //  })
      //  val popup_btn: Button = v.findViewById(R.id.popupbutton)

       // popup_btn.setOnClickListener(View.OnClickListener { popup_btn.setBackgroundColor(Color.RED) })

        imgViewPlay.setOnClickListener(this)
        imgClapStory.setOnClickListener(this)
        imgCancel.setOnClickListener(this)
        btnSendComment.setOnClickListener(this)

        val json=intent?.getStringExtra("storyData")

        storyData=Gson().fromJson(json,NearbyStory::class.java)


        with(storyData)
        {

            if(mediaUrl.contains(".mp3"))
            {

                resetAllViews()
               containerAudioPlayer.visibility=View.VISIBLE
                if (!isPlaying ) {
                    isPlaying = true
                    startPlaying()
                }

            }
            else if(mediaUrl.contains(".mp4"))
            {
                resetAllViews()
                videoAnimation.visibility=View.VISIBLE
                view_video.visibility=View.VISIBLE
                        val videoUri: Uri = Uri.parse(storyData?.mediaUrl)
        prepareVideoPlayer(videoUri,view_video)
            }
            else{

                resetAllViews()
                imgStory.visibility=View.VISIBLE
                Glide
                    .with(applicationContext)
                    .load(mediaUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(imgStory);
            }

            txtStoryMessage.setText(storyName)

            if (userData?.size > 0) let {
                Glide
                    .with(applicationContext)
                    .load(userData?.get(0)?.imageUrl)
                    .placeholder(R.drawable.man_cartoon)
                    .circleCrop()
                    .into(userImage);
userName.setText(userData.get(0).name)

            } else {
                userImage.setImageDrawable(application.resources.getDrawable(R.drawable
                    .man_cartoon))
            }
            if (storyClapData?.size > 0) let {

                storyClapCount.text = storyClapData?.size.toString()
                storyClapCounter=storyClapData?.size

            }
            else {storyClapCount.text = ""}

        }


        storyFullViewModel.clapsData.observe(this, Observer {

            if(it==1)
            {
                isClap=true
                storyClapCount.startAnimation(AnimationUtils.loadAnimation(applicationContext,R.anim.scale_anim))
                storyClapCounter+=1
                storyClapCount.setText(storyClapCounter.toString())
            }
            else
            {
                isClap=false
            }

        })

        //Observing comment data
        storyCommentViewModel.commentsData.observe(this, Observer {

           it?.let {
               edtComment.setText("")
               adapter.clearData()
               adapter.addData(it)
               adapter.notifyDataSetChanged()
           }
        })
        storyCommentViewModel.storyCommentsData.observe(this, Observer {

            it?.let {

                adapter.clearData()
                adapter.addData(it)
                adapter.notifyDataSetChanged()
            }
        })

        storyCommentViewModel.fetchStoryComments(storyData?._id)
        bindStoryComments()
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

    fun prepareVideoPlayer(uri: Uri?, videoview: VideoView)
    {

        try {
            // Start the MediaController
            val mediacontroller = MediaController(
                this
            )
            mediacontroller.setAnchorView(videoview)
            videoview.setMediaController(mediacontroller)

            videoview.setVideoURI(uri)

        } catch (e: java.lang.Exception) {
            Log.e("Error", e.message!!)
            e.printStackTrace()
        }

        videoview.requestFocus()
       // videoview.start()
        videoview.setOnPreparedListener(object : MediaPlayer.OnPreparedListener{
            override fun onPrepared(p0: MediaPlayer?) {

                videoview.start()
                videoAnimation.visibility=View.INVISIBLE

            }

        })
    }


    lateinit var adapter: StoryCommentsAdapter
    private fun bindStoryComments() {
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter = StoryCommentsAdapter(applicationContext,arrayListOf())
        recycler_view.addItemDecoration(
            DividerItemDecoration(
                recycler_view.context,
                (recycler_view.layoutManager as LinearLayoutManager).orientation
            )
        )
        recycler_view.adapter = adapter

    }

    //=========Audio Player
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    var fileName: String? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private var isPlaying = false

    private fun startPlaying() {
        audioAnimation.visibility=View.VISIBLE
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource( if (storyData?.mediaUrl.contains("https")) storyData?.mediaUrl.replace("https","http") else storyData?.mediaUrl)
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
            audioAnimation.visibility=View.INVISIBLE
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
                if (!isPlaying ) {
                    isPlaying = true
                    startPlaying()
                }
                else {
                    isPlaying = false
                    stopPlaying()
                }

            }
            R.id.imgCancel->{

                setResult(777)
                finish()
            }
            R.id.btnSendComment->{

                if(TextUtils.isEmpty(edtComment.text.toString()))
                {
                    toast(applicationContext,"comment is missing")
                    return
                }
                btnSendComment.startAnimation(AnimationUtils.loadAnimation(applicationContext,R.anim.scale_anim))
           storyCommentViewModel.addStoryComment(KotlinHelper.getSocialID(),storyData?._id,edtComment.text.toString())

            }
            R.id.imgClapStory->
            {


     val exist=storyData?.storyClapData?.find {it.SocialId.equals(KotlinHelper.getSocialID()) }
             if (exist==null&&isClap==false)
             {
                 imgClap.startAnimation(AnimationUtils.loadAnimation(applicationContext,R.anim.scale_anim))
                 storyFullViewModel.insetStoryClap(KotlinHelper.getSocialID(),storyData?._id)
             }
                else
             {
                 toast(this,"you have already clap this story")
             }

            }

        }
    }

    var isClap:Boolean=false
    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        audioAnimation.visibility=View.INVISIBLE
        //showing the play button
        imgViewPlay.setImageResource(R.drawable.ic_play_circle)
        chronometerAudio.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaying()
    }

    fun resetAllViews()
    {
        containerAudioPlayer.visibility=View.GONE
        view_video.visibility=View.GONE
        imgStory.visibility=View.GONE
    }
}