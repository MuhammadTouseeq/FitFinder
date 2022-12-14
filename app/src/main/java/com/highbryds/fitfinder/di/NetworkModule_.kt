package com.highbryds.fitfinder.di

import android.app.Application
import android.app.ProgressDialog
import android.view.Window
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.retrofit.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Retrofit at application level and singleton instance generated by hilt
@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule_ {

    @Singleton
    @Provides
    fun providesGsonBuilder() : Gson{
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit.Builder{

        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

        val okHttpClient = OkHttpClient().newBuilder().addNetworkInterceptor(interceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        return  Retrofit.Builder()
            .baseUrl("https://fitfinder110.herokuapp.com/")
           // .baseUrl("http://207.180.197.4:5000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideApiInterface(retrofit: Retrofit.Builder): ApiInterface{
        return retrofit.build().create(ApiInterface::class.java)
    }
}