package com.highbryds.fitfinder.model

data class UserStory(
    var storyName: String,
    var SocialId: String,
    var latitude: String,
    var longitude: String,
    var storyMediaPath: String?,
    var mediaUrl: String?,
    var chipText: String,
    var address: String,
    var enableCall: String?,
    var enableChat: String?,
    var helpCategory: String?,
    var desc: String?
)