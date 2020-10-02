package com.highbryds.fitfinder.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.highbryds.fitfinder.room.Tables.UserChat
import com.highbryds.fitfinder.room.Tables.UserMsgsList

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(items: UserChat?)

    @Insert()
    fun insertMsgsList(userMsgsList: UserMsgsList)

    @Query("Select * from UserMsgsList group by recipentID order by dateTime desc")
    fun getmsgs() : MutableList<UserMsgsList>

    @Query("Delete from UserMsgsList")
    fun deleteUserMsgsList()

    @Query("SELECT * from UserChat where RecipientId = :RecID OR SenderId= :myID AND RecipientId = :myID OR SenderId= :RecID order by id ASC")
    fun getallChat(RecID: String, myID: String): MutableList<UserChat>?

    @Query("SELECT count(*) from UserChat where MessageId=:messageid")
    fun getMessageCount(messageid: String?): Int
}