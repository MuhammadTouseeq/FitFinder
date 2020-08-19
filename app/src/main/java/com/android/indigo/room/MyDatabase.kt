package com.android.indigo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.indigo.room.tables.Users

@Database(entities = [Users::class] , version = 1 , exportSchema = true)
abstract class MyDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}