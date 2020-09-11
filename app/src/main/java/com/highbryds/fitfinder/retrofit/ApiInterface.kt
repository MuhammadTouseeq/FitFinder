package com.highbryds.fitfinder.retrofit

import com.highbryds.fitfinder.model.GeneralResponse
import com.highbryds.fitfinder.model.UserAgent
import com.highbryds.fitfinder.model.UserStoriesModel
import com.highbryds.fitfinder.model.UserStory
import com.highbryds.fitfinder.model.UsersData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import com.highbryds.fitfinder.model.WrapperStory
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("users")
    suspend fun getUsers(): Response<List<UsersData>>

    @POST("users/create")
    suspend fun createUsers(@Body usersData: UsersData): Response<UsersData>

    @Multipart
    @POST("users/UpdateProfilePicture")
    suspend fun uploadProfilePic(@Part profilePic: MultipartBody.Part , @Part("SocialId") name: RequestBody) : Response<GeneralResponse>

    @POST("users/update")
    suspend fun updateUsers(@Body usersData: UsersData): Response<UsersData>

    @POST("users/agent")
    suspend fun userAgent(@Body userAgent: UserAgent) : Response<GeneralResponse>

    @POST("users/getallStories/{socialID}")
    suspend fun userStories(@Path("socialID") socialID: String) : Response<List<UserStoriesModel>>

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