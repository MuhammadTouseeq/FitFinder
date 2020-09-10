package com.highbryds.fitfinder.retrofit

import com.highbryds.fitfinder.model.UserStory
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.model.WrapperStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("users")
    suspend fun getUsers(): Response<List<UsersData>>

    @Multipart
    @POST("uploadStories")
    suspend fun uploadStory(
        @Part storyMedia: MultipartBody.Part,
        @Part("storyName") storyName: RequestBody,
        @Part("SocialId") socialID: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody

    ): Response<UserStory>

    @POST("getnearbystories")
    suspend fun getAllNearByStories(
        @Query("lat") latitude: String,
        @Query("longi") longitude: String

    ): Response<WrapperStory>
}