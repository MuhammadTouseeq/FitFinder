package com.android.indigo.retrofit

import com.android.indigo.model.UsersData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET("users")
    suspend fun getUsers(): Response<List<UsersData>>

    @POST("users/create")
    suspend fun createUsers(@Body usersData: UsersData): Response<UsersData>

}