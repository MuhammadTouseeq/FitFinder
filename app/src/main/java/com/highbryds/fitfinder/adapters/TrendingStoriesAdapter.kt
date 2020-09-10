package com.highbryds.fitfinder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.model.TrendingStory
import kotlinx.android.synthetic.main.recycler_item_story_trending.view.*


class TrendingStoriesAdapter(
    private val arrData: ArrayList<TrendingStory>
) : RecyclerView.Adapter<TrendingStoriesAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: TrendingStory) {
          //  itemView.txtUserName.text = user.name
            //itemView.textViewUserEmail.text = user.email
//            Glide.with(itemView.imageViewAvatar.context)
//                .load(user.avatar)
//                .into(itemView.imageViewAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_item_story_trending, parent,
                false
            )
        )

    override fun getItemCount(): Int = arrData.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(arrData[position])

    fun addData(list: List<TrendingStory>) {
        arrData.addAll(list)
    }
}