package com.highbryds.fitfinder.room

import androidx.room.Dao
import androidx.room.Insert
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.room.tables.Users

@Dao
interface Dao {

    @Insert
    suspend fun insertUser(users: Users)
}