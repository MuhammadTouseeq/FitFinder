package com.highbryds.fitfinder.retrofit

import com.highbryds.fitfinder.model.UsersData
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("users")
    suspend fun getUsers(): Response<List<UsersData>>
}