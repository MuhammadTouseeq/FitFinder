package com.highbryds.fitfinder.room.Tables

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "UserMsgsList")
data class UserMsgsList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var messageID: String,
    var messageTXT: String,
    var recipentID: String,
    var dateTime: Long)