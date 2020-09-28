package com.highbryds.fitfinder.room


import androidx.room.Database
import androidx.room.RoomDatabase
import com.highbryds.fitfinder.room.Tables.UserChat

@Database(entities = [UserChat::class] , version = 1 , exportSchema = true)
abstract class MyDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}