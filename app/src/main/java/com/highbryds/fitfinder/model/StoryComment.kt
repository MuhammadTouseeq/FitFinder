package com.highbryds.fitfinder.model

import com.google.gson.annotations.SerializedName

data class StoryComment (
    var _id:String,
    var story_id: String,
    var SocialId:String,
    var comment:String,
    var user_data : UsersData,
    var createdAt:String)
