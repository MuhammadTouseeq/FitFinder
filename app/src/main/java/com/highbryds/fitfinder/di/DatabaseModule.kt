package com.highbryds.fitfinder.di

import android.app.Application
import com.highbryds.fitfinder.commonHelper.Constants.Companion.dbNAME

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

//    @Singleton
//    @Provides
//    fun getDatabase(application: Application): MyDatabase{
//        return Room.databaseBuilder(application , MyDatabase::class.java , dbNAME).fallbackToDestructiveMigration().build()
//    }
//
//    @Singleton
//    @Provides
//    fun getDatabaseDAO(myDatabase: MyDatabase): Dao{
//        return myDatabase.dao()
//    }
}