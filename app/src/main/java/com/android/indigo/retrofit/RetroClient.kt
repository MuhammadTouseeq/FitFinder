package com.android.indigo.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Deprecated("Use the NetworkModule in di pkg by Hilt")
object getRetroClient{

    val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
    }
}