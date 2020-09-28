package com.highbryds.fitfinder.retrofit

import com.highbryds.fitfinder.model.*
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

    @Multipart
    @POST("users/UpdateProfilePicture")
    suspend fun uploadProfilePic(
        @Part profilePic: MultipartBody.Part,
        @Part("SocialId") name: RequestBody
    ): Response<GeneralResponse>

    @POST("users/update")
    suspend fun updateUsers(@Body usersData: UsersData): Response<UsersData>

    @POST("users/agent")
    suspend fun userAgent(@Body userAgent: UserAgent): Response<GeneralResponse>

//    @POST("users/getallStories/{socialID}")
//    suspend fun userStories(@Path("socialID") socialID: String) : Response<List<UserStoriesModel>>

    @Multipart
    @POST("users/uploadStories")
    suspend fun uploadStory(
        @Part storyMedia: MultipartBody.Part,
        @Part("storyName") storyName: RequestBody,
        @Part("SocialId") socialID: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody

    ): Response<UserStory>

    @POST("users/getnearbystories")
    suspend fun getAllNearByStories(
        @Query("lat") latitude: String,
        @Query("longi") longitude: String

    ): Response<WrapperStory>

    @POST("users/create")
    suspend fun createUsers(@Body usersData: UsersData): Response<UsersData>


    @POST("users/InsertClap/{SocialId}/{storyid}")
    suspend fun insertStoryClap(
        @Path("SocialId") socialID: String,
        @Path("storyid") storyid: String
    ): Response<WrapperStory>

    @FormUrlEncoded
    @POST("users/insertComments")
    suspend fun insertStoryComment(
        @Field("SocialId") socialID: String,
        @Field("storyid") storyid: String,
        @Field("comment") comment: String
    ): Response<WrapperStoryComment>


    @POST("users/getallStories/{socialID}")
    suspend fun userStories(@Path("socialID") socialID: String): Response<List<NearbyStory>>

//    @Multipart
//    @POST("uploadStories")
//    suspend fun uploadStory(
//        @Part storyMedia: MultipartBody.Part,
//        @Part("storyName") storyName: RequestBody,
//        @Part("SocialId") socialID: RequestBody,
//        @Part("latitude") latitude: RequestBody,
//        @Part("longitude") longitude: RequestBody,
//        @Part("mediaUrl") mediaUrl: RequestBody
//
//    ): Response<UserStory>

    @FormUrlEncoded
    @POST("users/uploadStories")
    suspend fun uploadStory(
        @Field("storyName") storyName: String,
        @Field("SocialId") socialID: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("mediaUrl") mediaUrl: String,
        @Field("Category") Category: String
    ): Response<WrapperUploadStory>


//    @POST("users/getnearbystories")
//    suspend fun getAllNearByStories(
//        @Query("lat") latitude: String,
//        @Query("longi") longitude: String
//
//    ): Response<WrapperStory>

    @POST("users/logout/{SocialID}")
    suspend fun logoutUser(@Path("SocialID") SocialID: String): Response<GeneralResponse>

    @POST("users/deactiveStory/{id}")
    suspend fun deactivateStory(@Path("id") id: String): Response<GeneralResponse>

    @POST("users/getcomments/{storyid}")
    suspend fun getStorycomments(
        @Path("storyid") storyid: String
    ): Response<List<StoryComment>>

    @POST("users/deletecomment/{commentId}/{storyid}")
    suspend fun deletecomment(
        @Path("commentId") commentId: String,
        @Path("storyid") storyid: String
    ): Response<GeneralResponse>

    @POST("users/updateviews/{SocialId}/{StoryId}")
    suspend fun updateViews(
        @Path("SocialId") SocialId: String,
        @Path("StoryId") StoryId: String
    ): Response<WrapperStoryViews>



    @POST("users/gettrendingStories")
    suspend fun getTrendingStories(): Response<WrapperStory>
}