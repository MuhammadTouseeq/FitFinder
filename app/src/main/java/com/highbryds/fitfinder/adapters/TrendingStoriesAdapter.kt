package com.highbryds.fitfinder.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.NearbyStory
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.ui.Profile.UserStories
import com.highbryds.fitfinder.ui.StoryView.StoryFullViewActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_user_profile_main.*


class TrendingStoriesAdapter(var userStories: ArrayList<NearbyStory>,var context: Context): RecyclerView.Adapter<TrendingStoriesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById(R.id.IV_Story) as ImageView
        val storyType = itemView.findViewById(R.id.storyType) as ImageView
        val userImage = itemView.findViewById(R.id.userImage) as CircleImageView
        var clapCount = itemView.findViewById(R.id.clapCount) as TextView

        var txtUserName = itemView.findViewById(R.id.txtUserName) as TextView
        var viewsCount = itemView.findViewById(R.id.viewsCount) as TextView
        val RL_ViewStory = itemView.findViewById(R.id.RL_ViewStory) as RelativeLayout

        fun bindViews(userStories: NearbyStory, context: Context){

           with(userStories)
           {
               if (storyClapData != null && storyClapData?.size!! > 0) let {
                   clapCount.text = storyClapData?.size.toString()
               } else {
                   clapCount.text = ""
               }
               if (storyViewsData != null && storyViewsData?.size!! > 0) let {
                   viewsCount.text = storyViewsData?.size.toString()
               } else {
                   viewsCount.text=""
               }

               txtUserName.text=userData?.get(0)?.name
               txtUserName.isSelected=true
           }


            Glide
                .with(context)
                .load(userStories.mediaUrl)
                .placeholder(R.drawable.bg_round_green)
                .into(img);
            Glide
                .with(context)
                .load(userStories?.userData?.get(0)?.imageUrl)
                .placeholder(R.drawable.bg_round_green)
                .into(userImage);
            if (userStories.mediaUrl.contains(".mp4")){
                Glide
                    .with(context)
                    .load(R.drawable.video)
                    .placeholder(R.drawable.placeholder)
                    .into(storyType);
            }else if (userStories.mediaUrl.contains(".mp3")){
                Glide
                    .with(context)
                    .load(R.drawable.speaker)
                    .placeholder(R.drawable.placeholder)
                    .into(storyType);
            }else{
                Glide
                    .with(context)
                    .load(R.drawable.photo)
                    .placeholder(R.drawable.placeholder)
                    .into(storyType);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.trending_stories_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(userStories[position] , context)



        holder.RL_ViewStory.setOnClickListener{
            val intent = Intent(context, StoryFullViewActivity::class.java)
            val gson = Gson()
            val json = gson.toJson(userStories[position])
            intent.putExtra("storyData", json)
intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return  userStories.size
    }

    fun addData(list: List<NearbyStory>) {
        userStories?.addAll(list)
    }
}