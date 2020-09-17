package com.highbryds.fitfinder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.StoryCallback
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.ui.Profile.UserStories
import kotlinx.android.synthetic.main.activity_user_profile_main.*


class UserStoriesAdapter(var userStories: ArrayList<UserStoriesModel>,
                         var  storyCallback : StoryCallback,var context: Context): RecyclerView.Adapter<UserStoriesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById(R.id.IV_Story) as ImageView
        val storyType = itemView.findViewById(R.id.storyType) as ImageView
       // val title = itemView.findViewById(R.id.TV_title) as TextView
       // val TV_date = itemView.findViewById(R.id.TV_date) as TextView
        val CV_Story = itemView.findViewById(R.id.CV_Story) as ImageView

        fun bindViews(userStories: UserStoriesModel, context: Context){

//            if (userStories.name != null){
//                title.text = userStories.name
//            }else{
//                title.text = "No Story Name"
//            }
//
//            TV_date.text = userStories.date
            Glide
                .with(context)
                .load(userStories.img)
                .placeholder(R.drawable.bg_round_green)
                .into(img);

            if (userStories.img.contains(".mp4")){
                Glide
                    .with(context)
                    .load(R.drawable.video)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(storyType);
            }else if (userStories.img.contains(".mp3")){
                Glide
                    .with(context)
                    .load(R.drawable.speaker)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(storyType);
            }else{
                Glide
                    .with(context)
                    .load(R.drawable.photo)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(storyType);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.stories_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(userStories[position] , context)

        holder.CV_Story.setOnClickListener {
            storyCallback.storyItemPosition(position)
        }


    }

    override fun getItemCount(): Int {
        return  userStories.size
    }


}