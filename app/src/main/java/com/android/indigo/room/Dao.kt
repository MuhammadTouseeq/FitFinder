package com.android.indigo.room

import androidx.room.Dao
import androidx.room.Insert
import com.android.indigo.room.tables.Users

@Dao
interface Dao {

    @Insert
    suspend fun insertUser(users: Users)
}