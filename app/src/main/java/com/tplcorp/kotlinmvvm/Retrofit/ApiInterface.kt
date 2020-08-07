package com.tplcorp.kotlinmvvm.Retrofit

import com.tplcorp.kotlinmvvm.model.UsersData
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("users")
    suspend fun getUsers(): Response<List<UsersData>>
}