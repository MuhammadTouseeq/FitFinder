package com.highbryds.fitfinder.room


import androidx.room.Database
import androidx.room.RoomDatabase
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.room.Tables.UserMsgsList

@Database(entities = [UserChat::class , UserMsgsList::class] , version = 4 , exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}