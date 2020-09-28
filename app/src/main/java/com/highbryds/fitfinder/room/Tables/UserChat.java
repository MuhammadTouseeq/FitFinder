package com.highbryds.fitfinder.room.Tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.highbryds.fitfinder.commonHelper.DateConverter;

@Entity(tableName = "UserChat")
public class UserChat {

    // @PrimaryKey
    //@NonNull
    //@ColumnInfo(name = "MessageId")
    public String MessageId;


    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")

    public int id;


    public int getid() {
        return id;
    }

    public void setid(String id) {
        id = id;
    }


    public String getRecipientId() {
        return RecipientId;
    }

    public void setRecipientId(String recipientId) {
        RecipientId = recipientId;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String Message;
    public String RecipientId;
    public String SenderId;
    @TypeConverters(DateConverter.class)

    public long TimeStamp;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int Type;


}
