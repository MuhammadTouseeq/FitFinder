package com.highbryds.fitfinder.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.model.ProfileBioModel
import com.highbryds.fitfinder.ui.Profile.UpdateProfile
import com.highbryds.fitfinder.ui.Profile.UserStories

class ProfileMainAdapter(var profileData: ArrayList<ProfileBioModel>, var context: Context) :
    RecyclerView.Adapter<ProfileMainAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val Heading = itemView.findViewById(R.id.Heading) as TextView
        val About = itemView.findViewById(R.id.About) as TextView
        val UserStories = itemView.findViewById(R.id.UserStories) as TextView
        val UserClaps = itemView.findViewById(R.id.UserClaps) as TextView
        val UserComments = itemView.findViewById(R.id.UserComments) as TextView
        val IV_bio = itemView.findViewById(R.id.IV_bio) as ImageView
        val updateProfile = itemView.findViewById(R.id.updateProfile) as ImageView
        val storiesView = itemView.findViewById(R.id.stories) as ImageView

        fun bindViews(profileData: ProfileBioModel, context: Context) {

            Heading.text = profileData.title
            About.text = profileData.bio
            UserStories.text = profileData.storiesCount
            UserClaps.text = profileData.clapsCount
            UserComments.text = profileData.commentsCount

            Glide
                .with(context)
                .load(KotlinHelper.getUsersData().imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(IV_bio);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_bio_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(profileData[position], context)

        holder.updateProfile.setOnClickListener {
            val intent = Intent(context, UpdateProfile::class.java)
            context.startActivity(intent)
        }

        holder.storiesView.setOnClickListener {
            val intent = Intent(context, UserStories::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return profileData.size
    }


}