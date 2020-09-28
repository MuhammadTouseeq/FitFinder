package com.highbryds.fitfinder.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.highbryds.fitfinder.room.Tables.UserChat

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(items: UserChat?)

    @Query("SELECT * from UserChat where RecipientId = :id order by id ASC")
    fun getallChat(id: String): MutableList<UserChat>?

    @Query("SELECT count(*) from UserChat where MessageId=:messageid")
    fun getMessageCount(messageid: String?): Int
}