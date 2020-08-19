package com.android.indigo.di

import android.app.Application
import androidx.room.Room
import com.android.indigo.commonHelper.Constants.Companion.dbNAME
import com.android.indigo.room.Dao
import com.android.indigo.room.MyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun getDatabase(application: Application): MyDatabase{
        return Room.databaseBuilder(application , MyDatabase::class.java , dbNAME).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun getDatabaseDAO(myDatabase: MyDatabase): Dao{
        return myDatabase.dao()
    }
}