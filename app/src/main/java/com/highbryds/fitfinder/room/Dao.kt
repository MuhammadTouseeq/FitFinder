package com.highbryds.fitfinder.room

import androidx.lifecycle.LiveData
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

    @Query("update UserChat set isRead = 1 where SenderId = :socialID")
    fun updateReadStatus(socialID: String)


    @Insert()
    fun insertMsgsList(userMsgsList: UserMsgsList)

    /*//   @Query("Select * from UserChat group by SenderId order by id desc")
    @Query("Select max(id), * from UserChat group by SenderId ORDER by count(id) desc")
    fun getmsgs() : LiveData<List<UserChat>>
*/
    //Select *  from UserChat where SenderId in (Select distinct  SenderId  from UserChat where SenderId<>'107544249111821919185')

    //Select max(id),* from UserChat  where  SenderId in(Select distinct  SenderId  from UserChat where SenderId<>'107544249111821919185')
    //OR
    //   Select max(id),* from UserChat  where  SenderId in(Select distinct  SenderId  from UserChat where SenderId<>'107544249111821919185')
    //OR
    //   RecipientId  in (Select distinct  SenderId  from UserChat where SenderId<>'107544249111821919185')  group by RecipientId  order by id  desc


    /*  Select max(id),* from UserChat  where  SenderId in(Select distinct  SenderId  from UserChat where SenderId<>'107544249111821919185')
      OR
      RecipientId  in (Select distinct  SenderId  from UserChat where SenderId<>'107544249111821919185')  group by RecipientId  order by id  desc

  */

  //  @Query("Select max(id),* from UserChat  where  SenderId in(Select distinct  SenderId  from UserChat where SenderId<>:userSocialId) OR RecipientId  in (Select distinct  SenderId  from UserChat where SenderId<>:userSocialId)  group by RecipientId  order by id  desc")
    @Query("select * from UserChat order by id desc")
    fun getmsgs(): LiveData<List<UserChat>>


    @Query("Delete from UserMsgsList")
    fun deleteUserMsgsList()

    @Query("SELECT * from UserChat where RecipientId = :RecID OR SenderId= :myID AND RecipientId = :myID OR SenderId= :RecID order by id ASC")
    fun getallChat(RecID: String, myID: String): LiveData<List<UserChat>>?

//    @Query("SELECT count(*) from UserChat where MessageId=:messageid")
//    fun getMessageCount(messageid: String?): Int

}