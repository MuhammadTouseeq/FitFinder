package com.highbryds.fitfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.model.StoryComment
import com.highbryds.fitfinder.utils.Utils
import kotlinx.android.synthetic.main.recycler_item_story_trending.view.txtUserName
import kotlinx.android.synthetic.main.rv_item_story_comment.view.*


class StoryCommentsAdapter(val mcontext: Context,
    private val arrData: ArrayList<StoryComment>
) : RecyclerView.Adapter<StoryCommentsAdapter.DataViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_item_story_comment, parent,
                false
            )
        )

    override fun getItemCount(): Int = arrData.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(arrData[position],mcontext)

    fun addData(list: List<StoryComment>) {
        arrData.addAll(list)
    }

    fun clearData()
    {
        if(arrData?.size>0) arrData.clear()
        notifyDataSetChanged()
    }
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtUserName=itemView.txtUserName
        val txtComment=itemView.txtComment
        val imgProfile=itemView.imgProfile
        val txtDateTime=itemView.txtDateTime

        fun bind(
            model: StoryComment,
            mcontext: Context
        ) {

            with(model)
            {
                txtDateTime.setText(Utils.getDateTimeFromServer(createdAt,"EEE, d MMM yyyy h:mm a"))
                txtComment.text=comment
                txtUserName.text=user_data?.name
                user_data?. let {
                    Glide
                        .with(mcontext)
                        .load(user_data?.imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .circleCrop()
                        .into(imgProfile);

                }
            }

        }
    }

}