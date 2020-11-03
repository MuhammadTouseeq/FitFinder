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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;
    public String Message;
    public String MessageId;
    public String RecipientId;
    public String SenderId;
    @TypeConverters(DateConverter.class)
    public String TimeStamp;
    public int Type;
    public String recipientImage;
    public String recipientName;
    public String senderName;


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

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp; }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getRecipientImage() {
        return recipientImage;
    }

    public void setRecipientImage(String recipientImage) {
        this.recipientImage = recipientImage;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
