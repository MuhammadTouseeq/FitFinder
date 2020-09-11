package com.highbryds.fitfinder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.ui.Profile.UserStories
import kotlinx.android.synthetic.main.activity_user_profile_main.*


class UserStoriesAdapter(var userStories: ArrayList<UserStoriesModel>, var context: Context): RecyclerView.Adapter<UserStoriesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindViews(userStories: UserStoriesModel, context: Context){
            val img = itemView.findViewById(R.id.IV_Story) as ImageView
            val title = itemView.findViewById(R.id.TV_title) as TextView

            title.text = userStories.date
            Glide
                .with(context)
                .load(userStories.img)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(img);

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.stories_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(userStories[position] , context)

    }

    override fun getItemCount(): Int {
        return  userStories.size
    }


}